package com.example.ticketbookingsystem.validator.custom_annotations;

import com.example.ticketbookingsystem.validator.validation_classes.LocalizedNotBlankValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for localized not blank validation.
 */
@Constraint(validatedBy = LocalizedNotBlankValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalizedNotBlank {
    String message() default "error.not.blank";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}

