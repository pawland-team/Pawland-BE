package com.pawland.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PawLandException.class)
    public ResponseEntity PawLandExceptionHandler(PawLandException e) {

        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity invalidRequestHandler(MethodArgumentNotValidException e) {
        String[] errorMessages = e.getFieldErrors().stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .toArray(String[]::new);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorMessages);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity mailSendExceptionHandler(MailSendException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(e.getMessage());
    }
}
