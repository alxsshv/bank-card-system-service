package com.alxsshv.bank_card_system_service.validation;

import com.alxsshv.bank_card_system_service.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserIsPresentConstraintValidator implements ConstraintValidator<UserIsPresent, Long> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
        return userRepository.existsById(userId);
    }
}
