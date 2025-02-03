package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.ValidEventDateTime;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.time.LocalDateTime;
import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Validator class for managing sport event date time validation.
 */
@RequiredArgsConstructor
public class EventDateTimeValidator implements ConstraintValidator<ValidEventDateTime, LocalDateTime> {

    private final MessageSource messageSource;

    @Override
    public void initialize(ValidEventDateTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        boolean isValid = value != null && value.isAfter(LocalDateTime.now());

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            Locale locale = getLocale();

            String errorMessage = messageSource.getMessage("event.date.time.validation.error", null, locale);
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }
}