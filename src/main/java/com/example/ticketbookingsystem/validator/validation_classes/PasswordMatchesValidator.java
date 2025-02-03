package com.example.ticketbookingsystem.validator.validation_classes;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.validator.custom_annotations.PasswordMatches;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static com.example.ticketbookingsystem.utils.LocaleUtils.getLocale;

/**
 * Validator class for managing password matches validation during user registration.
 */
@RequiredArgsConstructor
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    private final MessageSource messageSource;

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        UserDto user = (UserDto) obj;
        boolean isValid = user.getPassword().equals(user.getConfirmPassword());

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            Locale locale = getLocale();

            String errorMessage = messageSource.getMessage("password.match.fail", null, locale);
            context.buildConstraintViolationWithTemplate(errorMessage)
                    .addConstraintViolation();
        }

        return isValid;
    }
}