package com.alxsshv.bank_card_system_service.service.implementation;

import com.alxsshv.bank_card_system_service.exception.RefreshTokenException;
import com.alxsshv.bank_card_system_service.model.RefreshToken;
import com.alxsshv.bank_card_system_service.repository.RefreshTokenRepository;
import com.alxsshv.bank_card_system_service.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken findByToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);
        if (refreshTokenOpt.isEmpty()) {
            throw new RefreshTokenException(token, "Refresh token не найден");
        }
        return refreshTokenOpt.get();
    }

    @Override
    public RefreshToken createRefreshToken(long userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .expireDate(Instant.now().plusMillis(refreshTokenExpiration.toMillis()))
                .token(UUID.randomUUID().toString())
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    @Override
    public void checkRefreshToken(RefreshToken token) {
        if (token.getExpireDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RefreshTokenException(token.getToken(), "Срок действия токена истёк"
                    + "Пожалуйста пройдите процедуру авторизации");
        }
    }

    @Override
    public void deleteByUserId(long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllByExpireDateLessThan(Instant.now());
    }
}
