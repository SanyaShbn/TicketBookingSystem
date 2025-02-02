package com.example.ticketbookingsystem.validator.custom_annotations;

import com.example.ticketbookingsystem.validator.validation_classes.PriceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PriceValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPrice {
    String message() default "{price.validation.fail}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}