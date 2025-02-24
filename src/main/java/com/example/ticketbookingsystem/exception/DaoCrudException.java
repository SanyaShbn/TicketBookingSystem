package com.example.ticketbookingsystem.exception;

/**
 * Exception thrown when getting errors while managing all the CRUD-operations on dao lawyer.
 */
public class DaoCrudException extends RuntimeException{
    public DaoCrudException(String message) {
        super(message);
    }

    public DaoCrudException(Throwable e) {
        super(e);
    }

    public DaoCrudException(String message, Throwable e) {
        super(message, e);
    }
}
