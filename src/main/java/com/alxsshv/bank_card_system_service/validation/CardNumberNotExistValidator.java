package com.alxsshv.bank_card_system_service.validation;


import com.alxsshv.bank_card_system_service.exception.EntityNotFoundException;
import com.alxsshv.bank_card_system_service.service.CardService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardNumberNotExistValidator implements ConstraintValidator<CardNumberNotExist, String> {
    @Autowired
    private final CardService cardService;

    @Override
    public boolean isValid(String number, ConstraintValidatorContext constraintValidatorContext) {
        number = number.replaceAll(" ", "");
        try {
            cardService.findByNumber(number);
        } catch (EntityNotFoundException ex) {
            return true;
        }
        return false;
    }
}
