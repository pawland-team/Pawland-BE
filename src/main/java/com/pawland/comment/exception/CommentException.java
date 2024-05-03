package com.pawland.comment.exception;

import com.pawland.global.exception.PawLandException;
import org.springframework.http.HttpStatus;

public class CommentException extends PawLandException {

    public CommentException(String message) {
        super(message);
    }

    public static class NotFoundComment extends CommentException {
        public NotFoundComment() {
            super(CommentExceptionMessage.COMMENT_NOT_FOUND.getMessage());
        }
    }

    public static class AccessDeniedException extends CommentException {
        public AccessDeniedException() {
            super(CommentExceptionMessage.ACCESS_DENIED_EXCEPTION.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
