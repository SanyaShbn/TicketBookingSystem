package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.validator.custom_annotations.ValidPrice;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Validator class for managing ticket price validity.
 */
@RequiredArgsConstructor
public class PriceValidator implements ConstraintValidator<ValidPrice, BigDecimal> {

    private final MessageSource messageSource;

    @Override
    public void initialize(ValidPrice constraintAnnotation) {
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        boolean isValid = value != null && value.compareTo(BigDecimal.ZERO) > 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            Locale locale = getLocale();

            String errorMessage = messageSource.getMessage("price.validation.fail", null, locale);
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }
}