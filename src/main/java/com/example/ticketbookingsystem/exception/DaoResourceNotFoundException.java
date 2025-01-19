package com.example.ticketbookingsystem.exception;

/**
 * Exception thrown when unable to get a specific entity by its ID from the database.
 */
public class DaoResourceNotFoundException extends RuntimeException{
    public DaoResourceNotFoundException(String message) {
        super(message);
    }
}
