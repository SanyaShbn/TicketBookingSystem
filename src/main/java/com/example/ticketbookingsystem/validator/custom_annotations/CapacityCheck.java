package com.example.ticketbookingsystem.validator.custom_annotations;

import com.example.ticketbookingsystem.validator.validation_classes.CapacityCheckValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for arena capacity check validation.
 */
@Constraint(validatedBy = CapacityCheckValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CapacityCheck {
    String message() default "{arena.capacity.error}";

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
