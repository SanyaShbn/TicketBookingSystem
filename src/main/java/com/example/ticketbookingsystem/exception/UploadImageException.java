package com.example.ticketbookingsystem.exception;

/**
 * Exception thrown when getting errors while managing the images uploading
 * (through second microservice - ImageService).
 */
public class UploadImageException extends RuntimeException{

    public UploadImageException(Throwable e) {
        super(e);
    }
}
