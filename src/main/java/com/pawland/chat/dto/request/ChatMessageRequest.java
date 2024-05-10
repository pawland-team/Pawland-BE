package com.pawland.chat.dto.request;

import com.pawland.chat.domain.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "채팅 메시지 전송 시 요청 값")
public class ChatMessageRequest {

    @NotBlank(message = "발신자 ID를 입력해주세요.")
    private String sender;

    @NotBlank(message = "메시지를 입력해주세요.")
    private String message;

    @Builder
    public ChatMessageRequest(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public ChatMessage toChatMessageWith(Long roomId) {
        return ChatMessage.builder()
            .roomId(roomId)
            .senderId(Long.parseLong(sender))
            .message(message)
            .build();
    }
}
