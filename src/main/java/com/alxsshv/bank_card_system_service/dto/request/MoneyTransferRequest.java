package com.alxsshv.bank_card_system_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Тело запроса на перевод денежных средств между картами пользователя")
public class MoneyTransferRequest {
    @Schema(description = "id карты, с которой необходимо списать денежные средства",
            example = "1")
    private long fromCardId;
    @Schema(description = "id карты, на которую необходимо перевести денежные средства",
            example = "1")
    private long toCardId;
    @NotBlank(message = "Сумма денежного перевода не может быть пустой")
    @Pattern(regexp = "^([0-9]{0,12}\\.?[0-9]{0,2})$", message = "Неверный формат суммы денежного перевода." +
            "Пожалуйста укажите сумму в рублях. Например для значения 100 рублей 50 копеек: 100.50")
    @Schema(description = "Сумма денежного перевода", example = "500.00")
    private String transferSumInRub;
}
