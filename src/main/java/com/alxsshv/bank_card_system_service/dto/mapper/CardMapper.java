package com.alxsshv.bank_card_system_service.dto.mapper;

import com.alxsshv.bank_card_system_service.dto.request.CreateCardRequest;
import com.alxsshv.bank_card_system_service.dto.response.CardDto;
import com.alxsshv.bank_card_system_service.exception.CardOperationException;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import com.alxsshv.bank_card_system_service.service.utils.CardNumberParser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class CardMapper {
    @Value("${app.card.expiration}")
    private Duration cardExpiration;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public Card mapToCard(CreateCardRequest request) {
        return Card.builder()
                .number(passwordEncoder.encode(request.getNumber().replaceAll(" ", "")))
                .publicNumberPart(CardNumberParser.getPublicNumber(request.getNumber()))
                .numberHash(request.getNumber().hashCode())
                .userId(request.getUserId())
                .expireDate(Instant.now().plusSeconds(cardExpiration.toSeconds()))
                .status(parseCardStatus(request.getStatus()))
                .balanceInRub(request.getBalance())
                .build();
    }

    public CardStatus parseCardStatus(String status) throws CardOperationException {
        try {
            return CardStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            return CardStatus.valueOfPseudonym(status);
        }
    }

    public CardDto mapToDto(Card card) {
        return CardDto.builder()
                .id(card.getId())
                .number("**** **** **** " + card.getPublicNumberPart())
                .userId(card.getUserId())
                .expireDate(card.getExpireDate())
                .status(card.getStatus())
                .build();
    }

    public Page<CardDto> mapToDtoPage(Page<Card> cards) {
        return cards.map(this::mapToDto);
    }
}
