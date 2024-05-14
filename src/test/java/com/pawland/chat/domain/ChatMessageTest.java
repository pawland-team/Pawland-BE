package com.pawland.chat.domain;


import com.pawland.chat.dto.request.ChatMessageRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ChatMessageTest {

    @DisplayName("빌더를 사용했을 때 ID는 자동으로 UUID로 자동 생성된다.")
    @Test
    void chatMessageBuilder() {
        // given
        ChatMessage result = ChatMessage.builder()
            .roomId(1L)
            .senderId(1L)
            .message("내용")
            .messageTime(LocalDateTime.now())
            .build();

        // expected
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getMessage()).isEqualTo("내용");
        assertThat(result.getMessageTime()).isInstanceOf(LocalDateTime.class);
    }

    @DisplayName("ChatMessage DTO를 엔티티로 변경할 때 ID는 UUID로 자동 생성된다.")
    @Test
    void toChatMessageWith() {
        // given
        ChatMessageRequest request = ChatMessageRequest.builder()
            .sender("1")
            .message("내용")
            .build();

        Long roomId = 1L;

        // when
        ChatMessage result = request.toChatMessageWith(roomId, LocalDateTime.now());
        System.out.println(result.getMessageTime());

        // then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getSenderId()).isEqualTo(1L);
        assertThat(result.getMessage()).isEqualTo("내용");
        assertThat(result.getMessageTime()).isInstanceOf(LocalDateTime.class);
    }
}