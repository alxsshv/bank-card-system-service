package com.alxsshv.bank_card_system_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoneyTransferRequest {
    private long fromCardId;
    private long toCardId;
    @NotBlank(message = "Сумма денежного перевода не может быть пустой")
    @Pattern(regexp = "^([0-9]{0,12}\\.?[0-9]{0,2})$", message = "Неверный формат суммы денежного перевода." +
            "Пожалуйста укажите сумму в рублях. Например для значения 100 рублей 50 копеек: 100.50")
    private String transferSumInRub;
}
