package com.pawland.comment.exception;

import lombok.Getter;

@Getter
public enum CommentExceptionMessage {
    COMMENT_NOT_FOUND("댓글을 찾을수 없습니다."),
    ACCESS_DENIED_EXCEPTION("변경 권한이 없습니다.");

    private final String message;

    CommentExceptionMessage(String message) {
        this.message = message;
    }
}
