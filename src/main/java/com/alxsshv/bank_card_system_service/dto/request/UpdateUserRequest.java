package com.alxsshv.bank_card_system_service.dto.request;

import com.alxsshv.bank_card_system_service.model.SystemRole;
import com.alxsshv.bank_card_system_service.validation.UsernameNotExist;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Тело запроса на обновление данных пользователя")
public class UpdateUserRequest {

    @NotNull(message = "Имя пользователя не указано")
    @Size(min = 3, max = 50, message = "Имя пользователя должно содержать не менее 3 и не более 50 символов")
    @Schema(description = "Имя пользователя для входа в систему (Имя пользователя изменить нельзя)",
            example = "Ivanov", minLength = 3, maxLength = 50)
    private String username;

    @NotNull(message = "Не указан адрес электронной почты")
    @Email(message = "Неверный формат адреса электронной почты. " +
            "Пожалуйста проверьте правильность указанного email")
    @Schema(description = "Адрес электронной почты пользователя",
            example = "Ivanov@email.com")
    private String email;

    @Builder.Default
    @Schema(description = "Набор ролей пользователей (определяют его права доступа)",
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]", defaultValue = "[\"ROLE_USER\"]")
    private Set<SystemRole> roles = Set.of(SystemRole.ROLE_USER);

    @NotNull(message = "Не указан пароль")
    @Size(min = 4, max = 50, message = "Пароль должен содержать не менее 4 и не более 50 символов")
    @Schema(description = "Пароль пользователя для входа в систему",
            example = "abc1234", minLength = 3, maxLength = 50)
    private String password;

}
