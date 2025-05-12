package com.alxsshv.bank_card_system_service.dto.response;

import com.alxsshv.bank_card_system_service.model.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardDto {

    private long id;

    private String number;

    private long userId;

    private Instant expireDate;

    private CardStatus status;
}
