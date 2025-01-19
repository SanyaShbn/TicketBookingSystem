package com.example.ticketbookingsystem.validator;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the result of a validation process.
 * It contains a list of validation errors, if any.
 */
@Getter
public class ValidationResult {
    private final List<Error> errors = new ArrayList<>();

    /**
     * Adds an error to the validation result.
     * @param error the error to add
     */
    public void add(Error error){
        this.errors.add(error);
    }

    /**
     * Checks if the validation result is valid, i.e., no errors are present.
     * @return true if no errors are present, false otherwise
     */
    public boolean isValid(){
        return errors.isEmpty();
    }
}

