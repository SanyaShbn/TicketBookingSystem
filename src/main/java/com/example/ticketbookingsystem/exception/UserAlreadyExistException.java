package com.example.ticketbookingsystem.exception;

/**
 * Exception thrown when trying to register an existing user.
 */
public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
