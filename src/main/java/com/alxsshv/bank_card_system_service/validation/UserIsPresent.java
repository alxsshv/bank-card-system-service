package com.alxsshv.bank_card_system_service.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserIsPresentConstraintValidator.class)
public @interface UserIsPresent {
    String message() default "Пользователя с указанным id не существует";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
