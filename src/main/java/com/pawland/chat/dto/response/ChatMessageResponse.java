package com.pawland.chat.dto.response;

import com.pawland.chat.domain.ChatMessage;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChatMessageResponse {

    private String messageId;
    private String sender;
    private String message;
    private String messageTime;

    @Builder
    public ChatMessageResponse(String messageId, String sender, String message, String messageTime) {
        this.messageId = messageId;
        this.sender = sender;
        this.message = message;
        this.messageTime = messageTime;
    }

    public static ChatMessageResponse from(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
            .messageId(chatMessage.getId())
            .sender(chatMessage.getSenderId().toString())
            .message(chatMessage.getMessage())
            .messageTime(chatMessage.getMessageTime().toString())
            .build();
    }
}
