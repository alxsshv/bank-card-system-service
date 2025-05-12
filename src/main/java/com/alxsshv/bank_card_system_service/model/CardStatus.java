package com.alxsshv.bank_card_system_service.model;

import com.alxsshv.bank_card_system_service.exception.CardOperationException;
import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import lombok.Getter;

@Getter
public enum CardStatus {
    ACTIVE("Активна"),
    BLOCKED("Заблокирована"),
    EXPIRED("Истек срок действия");

    private final String pseudonym;

    CardStatus(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public static CardStatus valueOfPseudonym(final String pseudonym) {
        for (CardStatus status : CardStatus.values()) {
            if (status.getPseudonym().equalsIgnoreCase(pseudonym)) {
                return status;
            }
        }
        throw new CardOperationException("Неверно указан статус карты."
                + "Пожалуйста укажите один из доступных статусов: Активна, Заблокирована, Истек срок действия");
    }
}
