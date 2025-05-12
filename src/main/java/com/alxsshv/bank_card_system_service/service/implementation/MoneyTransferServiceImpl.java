package com.alxsshv.bank_card_system_service.service.implementation;

import com.alxsshv.bank_card_system_service.configuration.AppConstants;
import com.alxsshv.bank_card_system_service.dto.request.MoneyTransferRequest;
import com.alxsshv.bank_card_system_service.exception.MoneyTransferException;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import com.alxsshv.bank_card_system_service.service.CardService;
import com.alxsshv.bank_card_system_service.service.MoneyTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MoneyTransferServiceImpl implements MoneyTransferService {
    @Autowired
    private final CardService cardService;
    private final MathContext mathContext = new MathContext(AppConstants.DECIMAL_PRECISION, RoundingMode.HALF_UP);

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void transferMoney(MoneyTransferRequest request, long userId) {
        final Card fromCard = cardService.findById(request.getFromCardId());
        final Card toCard = cardService.findById(request.getToCardId());
        final BigDecimal transferSumInRub = new BigDecimal(request.getTransferSumInRub(), mathContext);
        checkTransferCardOwner(fromCard, userId);
        checkTransfer(fromCard, toCard, transferSumInRub);

        BigDecimal toCardAfterTransferBalance = toCard.getBalanceInRub().add(transferSumInRub, mathContext)
                .setScale(AppConstants.DECIMAL_SCALE, RoundingMode.HALF_UP);
        BigDecimal fromCardAfterTransferBalance = fromCard.getBalanceInRub().subtract(transferSumInRub, mathContext)
                .setScale(AppConstants.DECIMAL_SCALE, RoundingMode.HALF_UP);

        toCard.setBalanceInRub(toCardAfterTransferBalance);
        fromCard.setBalanceInRub(fromCardAfterTransferBalance);
        cardService.saveAll(List.of(fromCard, toCard));
    }

    private void checkTransferCardOwner(Card card, long userId) {
        if (card.getUserId() != userId) {
            throw new MoneyTransferException("Пользователь не является владельцем указанных" +
                    " банковски карт. Перевод отклонен");
        }
    }

    private void checkTransfer(Card fromCard, Card toCard, BigDecimal transferSumInRub) {
        if (!fromCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new MoneyTransferException("Карта списания не активна. Операция денежного перевода отклонена");
        }
        if (!toCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new MoneyTransferException("Карта зачисления не активна. Операция денежного перевода отклонена");
        }
        if (fromCard.getUserId() != toCard.getUserId()) {
            throw new MoneyTransferException("Указанные карты принадлежат разным пользователям. " +
                    "Сервис поддерживает выполнение переводов только между счетами одного пользователя");
        }
        final boolean isNotEnoughFromCardBalance = (fromCard.getBalanceInRub().compareTo(transferSumInRub)) < 0;
        if (isNotEnoughFromCardBalance) {
            throw new MoneyTransferException("Для выполнения перевода на карте не достаточно средств");
        }
    }
}
