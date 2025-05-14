package com.alxsshv.bank_card_system_service.dto.response;

import com.alxsshv.bank_card_system_service.model.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Ответ на запрос получения информации о банковской карте")
public class CardDto {
    @Schema(description = "Идентификатор пользователя", example = "1")
    private long id;
    @Schema(description = "Уникальный номер карты с маскированием первых 12 символов",
            example = "**** **** **** 1234")
    private String number;
    @Schema(description = "Идентификатор пользователя",
            example = "1")
    private long userId;

    @Schema(description = "Дата истечения срока действия карты",
            example = "2007-12-03T10:15:30.00Z")
    private Instant expireDate;
    @Schema(description = "Статус карты", allowableValues = {"ACTIVE", "BLOCK", "EXPIRE"})
    private CardStatus status;
}
