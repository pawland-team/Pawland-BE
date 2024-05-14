package com.pawland.chat.dto.response;

import com.pawland.chat.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(name = "이전 채팅 내역 조회 시 응답 값")
public class ChatMessageHistoryResponse {

    private String nextCursor;
    private List<ChatMessageResponse> messageList;

    @Builder
    public ChatMessageHistoryResponse(String nextCursor, List<ChatMessage> chatMessageHistory) {
        this.nextCursor = nextCursor;
        this.messageList = chatMessageHistory.stream()
            .map(ChatMessageResponse::of)
            .toList();
    }

    public static ChatMessageHistoryResponse of(String nextCursor, List<ChatMessage> chatMessageHistory) {
        return ChatMessageHistoryResponse.builder()
            .nextCursor(nextCursor)
            .chatMessageHistory(chatMessageHistory)
            .build();
    }
}
