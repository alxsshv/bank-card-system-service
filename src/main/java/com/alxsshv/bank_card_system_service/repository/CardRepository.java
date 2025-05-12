package com.alxsshv.bank_card_system_service.repository;

import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

        Page<Card> findAllByStatusAndUserId(CardStatus cardStatus, long userId, Pageable pageable);

        Page<Card> findAllByExpireDateBetweenAndUserId(Instant startDate, Instant endDate, long userId, Pageable pageable);

        Page<Card> findAllByUserId(long userId, Pageable pageable);

        List<Card> findAllByExpireDateBefore(Instant currentDate);

        List<Card> findByPublicNumberPartAndNumberHash(String numberPublicPart, int numberHash);

        List<Card> findByPublicNumberPartAndNumberHashAndUserId(String numberPublicPart, int numberHash, long userId);
}
