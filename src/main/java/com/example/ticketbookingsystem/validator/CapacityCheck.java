package com.example.ticketbookingsystem.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = CapacityCheckValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface CapacityCheck {
    String message() default "{arena.capacity.error}";

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
