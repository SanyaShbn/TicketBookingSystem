package com.example.ticketbookingsystem.validator;

public interface Validator <T>{
    ValidationResult isValid(T t);
}
