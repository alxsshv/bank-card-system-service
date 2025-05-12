package com.alxsshv.bank_card_system_service.dto.request;

import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.validation.EmailNotExist;
import com.alxsshv.bank_card_system_service.validation.UsernameNotExist;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserRequest {

    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать не менее 3 и не более 50 символов")
    @UsernameNotExist
    private String username;

    @Email(message = "Неверный формат адреса электронной почты. " +
            "Пожалуйста проверьте правильность указанного email")
    @EmailNotExist
    private String email;

    @Size(min = 4, max = 50, message = "Пароль должен содержать не менее 4 и не более 50 символов")
    private String password;

}
