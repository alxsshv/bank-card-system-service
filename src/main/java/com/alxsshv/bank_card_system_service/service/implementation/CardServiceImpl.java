package com.alxsshv.bank_card_system_service.service.implementation;

import com.alxsshv.bank_card_system_service.dto.mapper.CardMapper;
import com.alxsshv.bank_card_system_service.dto.request.CreateCardRequest;
import com.alxsshv.bank_card_system_service.dto.response.CardDto;
import com.alxsshv.bank_card_system_service.exception.CardOperationException;
import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import com.alxsshv.bank_card_system_service.repository.CardRepository;
import com.alxsshv.bank_card_system_service.service.CardService;
import com.alxsshv.bank_card_system_service.service.utils.CardNumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    @Autowired
    private final CardRepository cardRepository;
    @Autowired
    private final CardMapper cardMapper;
    @Autowired
    private final PasswordEncoder encoder;

    @Override
    public void create(CreateCardRequest request) {
        final Card card = cardMapper.mapToCard(request);
        cardRepository.save(card);
    }

    @Override
    public void saveAll(List<Card> cards) {
        cardRepository.saveAll(cards);
    }

    @Override
    @Transactional
    public void activateById(long id) {
        Card card = findById(id);
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void blockById(long id) {
        Card card = findById(id);
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void blockByIdAndUserId(long cardId, long userId) {
        Card card = findById(cardId);
        if (card.getUserId() != userId) {
            throw new CardOperationException("Пользователь не является владельцем карты id = " + card.getId()
                    + ". Операция блокировки отклонена");
        }
        card.setStatus(CardStatus.BLOCKED);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void setExpireStatus() {
        List<Card> expiredCards = cardRepository.findAllByExpireDateBefore(Instant.now());
        expiredCards.forEach(card -> card.setStatus(CardStatus.EXPIRED));
        cardRepository.saveAll(expiredCards);
    }

    @Override
    public void deleteById(long id) {
        cardRepository.deleteById(id);
    }

    @Override
    public Card findById(long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Карта с id = " + id + " не найдена" ));
    }

    @Override
    public Card findByNumber(String number) {
        String publicNumberPart = CardNumberParser.getPublicNumber(number);
        List<Card> cards = cardRepository.findByPublicNumberPartAndNumberHash(publicNumberPart, number.hashCode());
        return cards.stream()
                .filter(card -> encoder.matches(number, card.getNumber()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Карта с указанным номером не найдена"));
    }

    @Override
    public Page<CardDto> findAllByNumberAndUserId(String number, long userId, Pageable pageable) {
        String publicNumberPart = CardNumberParser.getPublicNumber(number);
        List<Card> foundCards = cardRepository.findByPublicNumberPartAndNumberHashAndUserId(
                publicNumberPart, number.hashCode(), userId);
        List<CardDto> searchUsersCards = foundCards.stream()
                .filter(card -> encoder.matches(number, card.getNumber()))
                .map(cardMapper::mapToDto)
                .toList();
        return new PageImpl<>(searchUsersCards, pageable, searchUsersCards.size());

    }

    @Override
    public Page<CardDto> findAll(Pageable pageable) {
        final Page<Card> cards = cardRepository.findAll(pageable);
        return cardMapper.mapToDtoPage(cards);
    }

    @Override
    public Page<CardDto> findAllByUserId(long userId, Pageable pageable) {
        final Page<Card> cards =  cardRepository.findAllByUserId(userId, pageable);
        return cardMapper.mapToDtoPage(cards);
    }

    @Override
    public Page<CardDto> findAllByStatusAndUserId(String status, long userId, Pageable pageable) {
        final CardStatus cardStatus = parseCardStatus(status);
        final Page<Card> cards = cardRepository.findAllByStatusAndUserId(cardStatus, userId, pageable);
        return cardMapper.mapToDtoPage(cards);
    }

    private CardStatus parseCardStatus(String status) {
        try {
            return CardStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return CardStatus.valueOfPseudonym(status);
        }
    }

    @Override
    public Page<CardDto> findAllByExpireDateAndUserId(String startDate,
                                                      String endDate,
                                                      long userId,
                                                      Pageable pageable) {
        try {
            final Instant periodStart = Instant.parse(startDate);
            final Instant periodEnd = Instant.parse(endDate);
            final Page<Card> cards = cardRepository.findAllByExpireDateBetweenAndUserId(
                    periodStart, periodEnd, userId, pageable);
            return cardMapper.mapToDtoPage(cards);
        } catch (DateTimeParseException ex) {
            throw new CardOperationException("Неверный формат даты начала и конца" +
                    " периода поиска карт по сроку действия." +
                    "Формат должен соответствовать записи 2007-12-03T10:15:30.00Z");
        }
    }

    @Override
    public BigDecimal getCardBalanceById(long cardId, long userId) {
        Card card = findById(cardId);
        if (card.getUserId() != userId) {
            throw new CardOperationException("Пользователь id = "+ userId
                    + " не является владельцем карты Id = " + cardId + " Операция прервана");
        }
        return card.getBalanceInRub();
    }
}
