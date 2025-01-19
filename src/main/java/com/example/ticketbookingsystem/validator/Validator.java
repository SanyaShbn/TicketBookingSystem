package com.example.ticketbookingsystem.validator;

/**
 * Generic interface for validating entities.
 * @param <T> the type of the entity to validate
 */
public interface Validator <T>{
    ValidationResult isValid(T t);
}
