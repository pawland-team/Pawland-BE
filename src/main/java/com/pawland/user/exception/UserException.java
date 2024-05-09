package com.pawland.user.exception;

import com.pawland.global.exception.PawLandException;
import org.springframework.http.HttpStatus;

public class UserException extends PawLandException {

    public UserException(String message) {
        super(message);
    }

    public static class NotFoundUser extends UserException {
        public NotFoundUser() {
            super(UserExceptionMessage.USER_NOT_FOUND.getMessage());
        }
    }

    public static class AccessDeniedException extends UserException {
        public AccessDeniedException() {
            super(UserExceptionMessage.ACCESS_DENIED_EXCEPTION.getMessage());
        }
    }

    public static class AlreadyExistsEmail extends UserException {
        public AlreadyExistsEmail() {
            super(UserExceptionMessage.ALREADY_EXISTS_EMAIL.getMessage());
        }
    }

    public static class AlreadyExistsNickname extends UserException {
        public AlreadyExistsNickname() {
            super(UserExceptionMessage.ALREADY_EXISTS_NICKNAME.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
