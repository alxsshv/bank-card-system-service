package com.alxsshv.bank_card_system_service.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Тело ответа на запросы, не требующие возврата объектов")
public class SimpleResponse {
    @Schema(description = "Сообщение",
            example = "Операция выполнена успешно")
    private String message;
}
