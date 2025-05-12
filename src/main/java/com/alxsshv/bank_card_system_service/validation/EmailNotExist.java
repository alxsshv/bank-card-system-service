package com.alxsshv.bank_card_system_service.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailNotExistConstraintValidator.class)
public @interface EmailNotExist {
    String message() default "Адрес электронной почты уже используется." +
            " Пожалуйста укажите другой адрес электронной почты при регистрации " +
            " или используйте уже существующий аккаунт";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
