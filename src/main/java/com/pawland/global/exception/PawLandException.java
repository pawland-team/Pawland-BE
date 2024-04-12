package com.pawland.global.exception;

public abstract class PawLandException extends RuntimeException {

    public PawLandException(String message) {
        super(message);
    }

    public PawLandException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();
}
