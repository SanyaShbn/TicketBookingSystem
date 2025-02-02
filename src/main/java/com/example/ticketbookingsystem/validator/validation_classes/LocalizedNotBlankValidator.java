package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.LocalizedNotBlank;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Validator class for managing for localized not blank validation.
 */
@Component
@RequiredArgsConstructor
public class LocalizedNotBlankValidator implements ConstraintValidator<LocalizedNotBlank, String> {

    private final MessageSource messageSource;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            Locale locale = LocaleContextHolder.getLocale();
            String errorMessage = messageSource.getMessage("error.not.blank", null, locale);
            context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
            return false;
        }
        return true;
    }
}