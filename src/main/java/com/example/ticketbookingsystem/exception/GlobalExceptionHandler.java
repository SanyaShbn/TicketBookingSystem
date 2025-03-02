package com.example.ticketbookingsystem.exception;

import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DaoCrudException.class)
    public ResponseEntity<String> handleDaoCrudException(DaoCrudException ex) {
        log.error("CRUD exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request.");
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<String> handlePSQLException(PSQLException ex) {
        log.error("PSQLException exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while processing your request.");
    }

    @ExceptionHandler(DaoResourceNotFoundException.class)
    public ResponseEntity<String> handleDaoResourceNotFoundException(DaoResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found.");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
        log.error("Number format exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid number format.");
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<String> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        log.error("User already exists exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists.");
    }

    @ExceptionHandler(UploadImageException.class)
    public ResponseEntity<String> UploadImageException(UploadImageException ex) {
        log.error("UploadImageException occurred while uploading the image: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to upload image.");
    }

}