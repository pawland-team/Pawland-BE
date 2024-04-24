package com.pawland.global.exception;

import org.springframework.http.HttpStatus;

public class InvalidUserException extends PawLandException{

    private static final String MESSAGE = "이메일 인증이 인증되지 않은 유저입니다.";

    public InvalidUserException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }
}
