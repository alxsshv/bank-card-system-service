package com.alxsshv.bank_card_system_service.validation;

import com.alxsshv.bank_card_system_service.repository.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class UsernameIsPresentConstraintValidatorTest {
    @Autowired
    private ConstraintValidatorContext context;
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserIsPresentConstraintValidator validator
            = new UserIsPresentConstraintValidator(userRepository);

    @Test
    @DisplayName("Test isValid when user exist then return true")
    public void testIsValid_whenUserExist_thenReturnFalse() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        Assertions.assertTrue(validator.isValid(userId, context));
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    @DisplayName("Test isValid when user not exist found then return false")
    public void testIsValid_whenUserNotExist_thenReturnTrue() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);
        Assertions.assertFalse(validator.isValid(userId, context));
        verify(userRepository, times(1)).existsById(userId);
    }
}
