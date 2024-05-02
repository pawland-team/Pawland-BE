package com.pawland.post.exception;

import lombok.Getter;

@Getter
public enum PostExceptionMessage {
    POST_NOT_FOUND("게시글을 찾을수 없습니다."),
    ACCESS_DENIED_EXCEPTION("변경 권한이 없습니다.");

    private final String message;

    PostExceptionMessage(String message) {
        this.message = message;
    }
}
