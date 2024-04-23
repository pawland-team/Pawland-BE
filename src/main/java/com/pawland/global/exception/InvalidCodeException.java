package com.pawland.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidCodeException extends PawLandException{

    private static final String MESSAGE = "인증번호를 확인해주세요.";

    public InvalidCodeException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
