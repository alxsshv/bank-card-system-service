package com.alxsshv.bank_card_system_service.web.controller;

import com.alxsshv.bank_card_system_service.dto.request.LoginRequest;
import com.alxsshv.bank_card_system_service.dto.request.RefreshTokenRequest;
import com.alxsshv.bank_card_system_service.dto.request.RegisterUserRequest;
import com.alxsshv.bank_card_system_service.dto.response.AuthResponse;
import com.alxsshv.bank_card_system_service.dto.response.RefreshTokenResponse;
import com.alxsshv.bank_card_system_service.dto.response.SimpleResponse;
import com.alxsshv.bank_card_system_service.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Авторизация пользователей",
        description = "Регистрация, вход в систему, обновление JWT токена, выход из системы")
public class AuthController {

    @Autowired
    private final SecurityService securityService;

    @Operation(
            summary = "Вход в систему",
            description = "Метод позволяет войти в систему," +
                    " используя логин или адрес электронной почты и пароль"
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Validated @RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = securityService.loginUser(loginRequest);
        String message = "Пользователь " + loginRequest.getLogin() + " подключился к сервису";
        log.info(message);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(
            summary = "Регистрация пользователя",
            description = "Позволяет зарегистрироваться пользователю"
    )
    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> registerUser(@Validated @RequestBody RegisterUserRequest request) {
        securityService.registerUser(request);
        final String message = "Пользователь " + request.getUsername() + " успешно зарегистрирован.";
        log.info(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SimpleResponse(message));
    }

    @Operation(
            summary = "Обновление токена",
            description = "Позволяет обновит пару refresh-токен," +
                    " access-токен используя ранее выданный refresh-токен"
    )
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(securityService.refreshToken(request));
    }

    @Operation(
            summary = "Выход пользователя из системы"
    )
    @PostMapping("/logout")
    public ResponseEntity<SimpleResponse> logout(@AuthenticationPrincipal UserDetails userDetails) {
        securityService.logout();
        String message = "Пользователь " + userDetails.getUsername() + " завершил работу с сервисом";
        log.info(message);
        return ResponseEntity.ok(new SimpleResponse(message));
    }

}
