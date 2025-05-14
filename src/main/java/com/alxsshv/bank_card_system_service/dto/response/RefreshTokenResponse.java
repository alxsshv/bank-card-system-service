package com.alxsshv.bank_card_system_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ на запрос обновления токена")
public class RefreshTokenResponse {
    @Schema(description = "Access token выданный при входе в систему",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc0NzIyMzYzNywiZXhwIjo"
                    + "xNzQ3MjIzOTM3fQ.1t--rKjkmK0as3fqIZC6SRRMvdCYE8HMQr5ib5qFczk")
    private String accessToken;
    @Schema(description = "Refresh token выданный при входе в систему",
            example = "5c9232d5-ce1c-4e03-b620-6251aebe6cdb")
    private String refreshToken;
}
