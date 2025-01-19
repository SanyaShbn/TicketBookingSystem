package com.example.ticketbookingsystem.exception;

/**
 * Exception thrown when getting errors while managing create and update operations on dao lawyer.
 */
public class CreateUpdateEntityException extends RuntimeException{
    public CreateUpdateEntityException(String message) {
        super(message);
    }
}
