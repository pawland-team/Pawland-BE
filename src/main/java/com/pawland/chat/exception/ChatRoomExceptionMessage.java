package com.pawland.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatRoomExceptionMessage {

    CHATROOM_NOT_FOUND("해당 채팅방을 찾을 수 없습니다.");

    private final String message;
}
