package com.pawland.post.exception;

import com.pawland.global.exception.PawLandException;
import org.springframework.http.HttpStatus;

public class PostException extends PawLandException {
    public PostException(String message) {
        super(message);
    }

    public static class NotFoundException extends PostException {
        public NotFoundException() {
            super(PostExceptionMessage.POST_NOT_FOUND.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
