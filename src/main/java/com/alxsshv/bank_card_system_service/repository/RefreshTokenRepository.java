package com.alxsshv.bank_card_system_service.repository;

import com.alxsshv.bank_card_system_service.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteAllByUserId(Long userId);

    void deleteAllByExpireDateLessThan(Instant currentDateTime);
}
