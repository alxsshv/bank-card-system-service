package com.alxsshv.bank_card_system_service.service;

import com.alxsshv.bank_card_system_service.dto.request.CreateCardRequest;
import com.alxsshv.bank_card_system_service.dto.response.CardDto;
import com.alxsshv.bank_card_system_service.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface CardService {
    void create(CreateCardRequest createCardResponse);
    void saveAll(List<Card> cards);
    void activateById(long id);
    void blockById(long id);
    void blockByIdAndUserId(long cardId, long userId);
    void setExpireStatus();
    void deleteById(long id);
    Card findById(long id);
    Card findByNumber(String number);
    Page<CardDto> findAll(Pageable pageable);
    Page<CardDto> findAllByUserId(long userId, Pageable pageable);
    Page<CardDto> findAllByNumberAndUserId(String number, long userId, Pageable pageable);
    Page<CardDto> findAllByStatusAndUserId(String status, long userId, Pageable pageable);
    Page<CardDto> findAllByExpireDateAndUserId(String startDate, String endDate, long userId, Pageable pageable);
    BigDecimal getCardBalanceById(long cardId, long userId);
}
