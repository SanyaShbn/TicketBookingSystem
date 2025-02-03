package com.example.ticketbookingsystem.validator.custom_annotations;

import com.example.ticketbookingsystem.validator.validation_classes.SectorNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for sector name validation.
 */
@Constraint(validatedBy = SectorNameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSectorName {
    String message() default "invalid.sector.name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}