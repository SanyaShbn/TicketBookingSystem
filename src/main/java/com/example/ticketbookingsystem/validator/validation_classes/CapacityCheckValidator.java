package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.CapacityCheck;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Validator class for checking the capacity constraints of an arena.
 */
@RequiredArgsConstructor
public class CapacityCheckValidator implements ConstraintValidator<CapacityCheck, Integer> {

    private final MessageSource messageSource;
    private int min;
    private int max;

    @Override
    public void initialize(CapacityCheck constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null || value < min || value > max) {
            context.disableDefaultConstraintViolation();
            Locale locale = getLocale();
            String errorMessage = messageSource.getMessage("arena.capacity.error", new Object[]{min, max}, locale);
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            return false;
        }
        return true;
    }
}

