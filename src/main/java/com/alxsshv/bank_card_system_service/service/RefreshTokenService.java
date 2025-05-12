package com.alxsshv.bank_card_system_service.service;

import com.alxsshv.bank_card_system_service.exception.RefreshTokenException;
import com.alxsshv.bank_card_system_service.model.RefreshToken;

public interface RefreshTokenService {
    RefreshToken findByToken(String token);
    RefreshToken createRefreshToken(long userId);
    void checkRefreshToken(RefreshToken token) throws RefreshTokenException;
    void deleteByUserId(long userId);
    void deleteExpiredTokens();
}
