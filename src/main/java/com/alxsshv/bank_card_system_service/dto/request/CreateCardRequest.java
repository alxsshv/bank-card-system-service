package com.alxsshv.bank_card_system_service.dto.request;

import com.alxsshv.bank_card_system_service.validation.CardNumberNotExist;
import com.alxsshv.bank_card_system_service.validation.UserIsPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCardRequest {

    @NotNull(message = "Пожалуйста укажите номер карты")
    @Pattern(regexp = "^([0-9]{4}\\s?){4}$", message = "Неверный формат номера карты. " +
            "Пожалуйста введите номер карты в формате: 1111 1111 1111 1111")
    @CardNumberNotExist
    private String number;

    @NotNull(message = "Укажите id владельца карты")
    @UserIsPresent
    private long userId;

    @NotBlank(message = "Пожалуйста укажите статус карты: ACTIVE, BLOCK, EXPIRE")
    private String status;

    @Builder.Default
    private BigDecimal balance = new BigDecimal("0.00");
}
