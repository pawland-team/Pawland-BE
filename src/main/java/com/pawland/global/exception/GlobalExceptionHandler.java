package com.pawland.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PawLandException.class)
    public ResponseEntity PawLandExceptionHandler(PawLandException e) {

        return ResponseEntity
            .status(e.getStatusCode())
            .body(e.getMessage());
    }
}
