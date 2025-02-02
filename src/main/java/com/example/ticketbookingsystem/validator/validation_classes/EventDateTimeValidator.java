package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.ValidEventDateTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

/**
 * Validator class for managing sport event date time validation.
 */
public class EventDateTimeValidator implements ConstraintValidator<ValidEventDateTime, LocalDateTime> {
    @Override
    public void initialize(ValidEventDateTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(LocalDateTime.now());
    }
}