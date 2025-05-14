package com.alxsshv.bank_card_system_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Тело запроса авторизации пользователя")
public class LoginRequest {

    @NotBlank(message = "Пожалуйста введите имя пользователя" +
            " или адрес электронной почты")
    @Schema(description = "Имя пользователя или email для входа в систему",
            example = "Ivanov или Ivanov@email.com", minLength = 3, maxLength = 50)
    private String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Schema(description = "Пароль пользователя для входа в систему",
            example = "abc1234", minLength = 3, maxLength = 50)
    private String password;
}
