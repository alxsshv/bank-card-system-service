package com.alxsshv.bank_card_system_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Пожалуйста введите имя пользователя" +
            " или адрес электронной почты")
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
