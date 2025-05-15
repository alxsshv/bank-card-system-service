package com.alxsshv.bank_card_system_service.validation;

import com.alxsshv.bank_card_system_service.repository.UserRepository;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;

public class UsernameNotExistConstraintValidatorTest {
    @Autowired
    private ConstraintValidatorContext context;
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UsernameNotExistConstraintValidator validator
            = new UsernameNotExistConstraintValidator(userRepository);

    @Test
    @DisplayName("Test isValid when user exist then return false")
    public void testIsValid_whenUserExist_thenReturnFalse() {
        String username = "User";
        when(userRepository.existsByUsername(username)).thenReturn(true);
        Assertions.assertFalse(validator.isValid(username, context));
        verify(userRepository, times(1)).existsByUsername(username);
    }

    @Test
    @DisplayName("Test isValid when user not exist found then return true")
    public void testIsValid_whenUserNotExist_thenReturnTrue() {
        String username = "User";
        when(userRepository.existsByUsername(username)).thenReturn(false);
        Assertions.assertTrue(validator.isValid(username, context));
        verify(userRepository, times(1)).existsByUsername(username);
    }
}
