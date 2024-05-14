package com.pawland.chat.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String id;

    @NotNull
    private Long roomId;

    @NotNull
    private Long senderId;

    @NotBlank
    private String message;

    private LocalDateTime messageTime;

    @Builder
    public ChatMessage(Long roomId, String message, Long senderId, LocalDateTime messageTime) {
        this.id = UUID.randomUUID().toString();
        this.roomId = roomId;
        this.message = message;
        this.senderId = senderId;
        this.messageTime = messageTime;
    }
}
