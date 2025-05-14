package com.alxsshv.bank_card_system_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Schema(description = "Тело ответа на запрос авторизации")
public class AuthResponse {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private long id;
    @Schema(description = "Access token выданный при входе в систему",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NzIyMzYzNywiZXhwIjo"
                    + "xNzQ3MjIzOTM3fQ.1t--rKjkmK0as3fqIZC6SRRMvdCYE8HMQr5ib5qFczk")
    private String token;
    @Schema(description = "Refresh token выданный при входе в систему",
            example = "5c9232d5-ce1c-4e03-b620-6251aebe6cdb")
    private String refreshToken;
    @Schema(description = "Имя пользователя для входа в систему",
            example = "Ivanov", minLength = 3, maxLength = 50)
    private String username;
    @Schema(description = "Адрес электронной почты пользователя",
            example = "Ivanov@email.com")
    private String email;
    @Schema(description = "Набор ролей пользователей (определяют его права доступа)",
            example = "[\"ROLE_USER\", \"ROLE_ADMIN\"]")
    private List<String> roles;
}
