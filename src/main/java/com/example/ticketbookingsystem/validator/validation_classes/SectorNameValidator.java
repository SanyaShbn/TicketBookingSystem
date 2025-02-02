package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.ValidSectorName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator class for managing sector name validity.
 */
public class SectorNameValidator implements ConstraintValidator<ValidSectorName, String> {

    @Override
    public void initialize(ValidSectorName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("[A-Z]");
    }
}