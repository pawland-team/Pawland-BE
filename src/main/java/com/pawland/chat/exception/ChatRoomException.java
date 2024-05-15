package com.pawland.chat.exception;

import com.pawland.global.exception.PawLandException;
import com.pawland.user.exception.UserException;

import static com.pawland.chat.exception.ChatRoomExceptionMessage.CHATROOM_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class ChatRoomException extends PawLandException {


    public ChatRoomException(String message) {
        super(message);
    }

    public static class ChatRoomNotFound extends UserException {
        public ChatRoomNotFound() {
            super(CHATROOM_NOT_FOUND.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return BAD_REQUEST.value();
    }
}
