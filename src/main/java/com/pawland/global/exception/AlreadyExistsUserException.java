package com.pawland.global.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsUserException extends PawLandException{

    private static final String MESSAGE = "이미 존재하는 유저입니다.";

    public AlreadyExistsUserException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
