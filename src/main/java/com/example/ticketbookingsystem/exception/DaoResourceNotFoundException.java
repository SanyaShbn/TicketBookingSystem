package com.example.ticketbookingsystem.exception;

public class DaoResourceNotFoundException extends RuntimeException{
    public DaoResourceNotFoundException(String message) {
        super(message);
    }
}
