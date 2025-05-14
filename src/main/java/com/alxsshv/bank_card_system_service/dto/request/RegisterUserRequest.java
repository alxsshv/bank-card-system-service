package com.alxsshv.bank_card_system_service.dto.request;

import com.alxsshv.bank_card_system_service.validation.EmailNotExist;
import com.alxsshv.bank_card_system_service.validation.UsernameNotExist;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Тело запроса на регистрацию пользователя")
public class RegisterUserRequest {

    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать не менее 3 и не более 50 символов")
    @UsernameNotExist
    @Schema(description = "Имя пользователя для входа в систему",
            example = "Ivanov", minLength = 3, maxLength = 50)
    private String username;

    @Email(message = "Неверный формат адреса электронной почты. " +
            "Пожалуйста проверьте правильность указанного email")
    @EmailNotExist
    @Schema(description = "Адрес электронной почты пользователя",
            example = "Ivanov@email.com")
    private String email;

    @Size(min = 4, max = 50, message = "Пароль должен содержать не менее 4 и не более 50 символов")
    @Schema(description = "Пароль пользователя для входа в систему",
            example = "abc1234", minLength = 3, maxLength = 50)
    private String password;

}
