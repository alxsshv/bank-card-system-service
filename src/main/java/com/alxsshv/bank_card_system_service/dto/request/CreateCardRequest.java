package com.alxsshv.bank_card_system_service.dto.request;

import com.alxsshv.bank_card_system_service.validation.CardNumberNotExist;
import com.alxsshv.bank_card_system_service.validation.UserIsPresent;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Тело запроса на создание банковской карты")
public class CreateCardRequest {

    @NotNull(message = "Пожалуйста укажите номер карты")
    @Pattern(regexp = "^([0-9]{4}\\s?){4}$", message = "Неверный формат номера карты. " +
            "Пожалуйста введите номер карты в формате: 1111 1111 1111 1111")
    @CardNumberNotExist
    @Schema(description = "Уникальный номер карты",
            example = "1234 1234 1234 1234")
    private String number;

    @NotNull(message = "Укажите id владельца карты")
    @UserIsPresent
    @Schema(description = "Идентификатор пользователя",
            example = "1")
    private long userId;

    @NotBlank(message = "Пожалуйста укажите статус карты: ACTIVE, BLOCK, EXPIRE")
    @Schema(description = "Статус карты", allowableValues = {"ACTIVE", "BLOCK", "EXPIRE"})
    private String status;

    @Builder.Default
    @Schema(description = "Баланс карты", example = "1000,00", defaultValue = "0.00")
    private BigDecimal balance = new BigDecimal("0.00");
}
