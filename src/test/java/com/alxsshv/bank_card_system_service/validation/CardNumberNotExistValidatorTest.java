package com.alxsshv.bank_card_system_service.validation;

import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.model.Card;
import com.alxsshv.bank_card_system_service.service.CardService;
import com.alxsshv.bank_card_system_service.service.implementation.CardServiceImpl;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class CardNumberNotExistValidatorTest {
    @Autowired
    private ConstraintValidatorContext context;
    private final CardService cardService = mock(CardServiceImpl.class);
    private final CardNumberNotExistValidator validator = new CardNumberNotExistValidator(cardService);

    @Test
    @DisplayName("Test isValid when card is found then return false")
    public void testIsValid_whenCardIsFound_thenReturnFalse() {
        String cardNumber = "1234123412341234";
        when(cardService.findByNumber(cardNumber)).thenReturn(new Card());
        Assertions.assertFalse(validator.isValid(cardNumber, context));
        verify(cardService, times(1)).findByNumber(anyString());
    }

    @Test
    @DisplayName("Test isValid when card is not found then return true")
    public void testIsValid_whenCardIsNotFound_thenReturnTrue() {
        String cardNumber = "1234123412341234";
        when(cardService.findByNumber(cardNumber)).thenThrow(EntityNotFoundException.class);
        Assertions.assertTrue(validator.isValid(cardNumber, context));
        verify(cardService, times(1)).findByNumber(cardNumber);
    }
}
