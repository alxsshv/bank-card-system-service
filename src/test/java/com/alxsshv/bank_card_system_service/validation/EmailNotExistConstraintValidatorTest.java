package com.alxsshv.bank_card_system_service.validation;

import com.alxsshv.bank_card_system_service.repository.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class EmailNotExistConstraintValidatorTest {
    @Autowired
    private ConstraintValidatorContext context;
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final EmailNotExistConstraintValidator validator
            = new EmailNotExistConstraintValidator(userRepository);

    @Test
    @DisplayName("Test isValid when user exist then return false")
    public void testIsValid_whenUserExists_thenReturnFalse() {
        String email = "user@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);
        Assertions.assertFalse(validator.isValid(email, context));
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("Test isValid when user not exist found then return true")
    public void testIsValid_whenUserNotExists_thenReturnTrue() {
        String email = "user@email.com";
        when(userRepository.existsByEmail(email)).thenReturn(false);
        Assertions.assertTrue(validator.isValid(email, context));
        verify(userRepository, times(1)).existsByEmail(email);
    }
}
