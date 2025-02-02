package com.example.ticketbookingsystem.validator.custom_annotations;

import com.example.ticketbookingsystem.validator.validation_classes.PasswordMatchesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Documented
public @interface PasswordMatches {
    String message() default "{password.match.fail}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}