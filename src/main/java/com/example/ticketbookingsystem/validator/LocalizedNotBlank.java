package com.example.ticketbookingsystem.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = LocalizedNotBlankValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalizedNotBlank {
    String message() default "{error.not.blank}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

