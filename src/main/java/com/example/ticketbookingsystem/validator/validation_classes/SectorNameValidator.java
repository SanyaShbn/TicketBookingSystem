package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.ValidSectorName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Validator class for managing sector name validity.
 */
@RequiredArgsConstructor
public class SectorNameValidator implements ConstraintValidator<ValidSectorName, String> {

    private final MessageSource messageSource;

    @Override
    public void initialize(ValidSectorName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value != null && value.matches("[A-Z]");

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            Locale locale = getLocale();

            String errorMessage = messageSource.getMessage("invalid.sector.name", null, locale);
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }
}