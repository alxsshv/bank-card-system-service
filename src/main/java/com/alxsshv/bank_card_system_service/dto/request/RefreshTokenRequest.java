package com.alxsshv.bank_card_system_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Тело запроса на обновление токеа")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token выданный при входе в систему",
            example = "5c9232d5-ce1c-4e03-b620-6251aebe6cdb")
    private String refreshToken;
}
