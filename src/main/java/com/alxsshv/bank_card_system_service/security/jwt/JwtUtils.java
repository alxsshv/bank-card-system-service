package com.alxsshv.bank_card_system_service.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    public String generateJwtToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + tokenExpiration.toMillis()))
                .signWith(generateSignatureSecretKey(jwtSecret))
                .compact();
    }

    private SecretKey generateSignatureSecretKey(String secretKey){
        byte[] SecretKeyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(SecretKeyBytes);
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateSignatureSecretKey(jwtSecret))
                .build()
                .parseSignedClaims(token).getPayload();
    }

    public boolean validate(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Неверная сигнатура токена: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Недействительный токен: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Токен просрочен: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Токен не поддерживается: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Токен не содержит данные или данные не корректны: {}", ex.getMessage());
        }
        return false;
    }

}
