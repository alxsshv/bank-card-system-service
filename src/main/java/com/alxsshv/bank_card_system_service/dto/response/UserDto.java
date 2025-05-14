package com.alxsshv.bank_card_system_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @Schema(description = "Идентификатор пользователя",
            example = "1")
    private long id;
    @Schema(description = "Имя пользователя для входа в систему",
            example = "Ivanov", minLength = 3, maxLength = 50)
    private String username;
    @Schema(description = "Адрес электронной почты пользователя",
            example = "Ivanov@email.com")
    private String email;
    @Schema(description = "Набор ролей пользователей (определяют его права доступа)",
            example = "[\"ROLE_USER\"]")
    private List<String> roles;
}
