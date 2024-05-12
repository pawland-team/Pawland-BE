package com.pawland.chat.dto.response;

import com.pawland.chat.domain.ChatMessage;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessageHistoryResponse {

    private String nextCursor;
    private List<ChatMessageResponse> messageList;

    @Builder
    public ChatMessageHistoryResponse(String nextCursor, List<ChatMessage> chatMessageHistory) {
        this.nextCursor = nextCursor;
        this.messageList = chatMessageHistory.stream()
            .map(ChatMessageResponse::from)
            .toList();
    }

    public static ChatMessageHistoryResponse of(String nextCursor, List<ChatMessage> chatMessageHistory) {
        return ChatMessageHistoryResponse.builder()
            .nextCursor(nextCursor)
            .chatMessageHistory(chatMessageHistory)
            .build();
    }
}
