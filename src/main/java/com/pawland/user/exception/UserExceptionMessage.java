package com.pawland.user.exception;

import lombok.Getter;

@Getter
public enum UserExceptionMessage {
    USER_NOT_FOUND("유저를 찾을수 없습니다."),
    ACCESS_DENIED_EXCEPTION("변경 권한이 없습니다."),
    ALREADY_EXISTS_EMAIL("이미 존재하는 이메일입니다."),
    ALREADY_EXISTS_NICKNAME("이미 존재하는 닉네임입니다.");
    private final String message;

    UserExceptionMessage(String message) {
        this.message = message;
    }
}
