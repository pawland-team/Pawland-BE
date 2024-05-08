package com.pawland.chat.dto.request;

import com.pawland.chat.domain.ChatRoom;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequest {

    @NotNull
    private Long sellerId;

    @NotNull
    private Long productId;

    @Builder
    public ChatRoomCreateRequest(Long sellerId, Long productId) {
        this.sellerId = sellerId;
        this.productId = productId;
    }

    public ChatRoom toChatRoomWithMyId(Long buyerId) {
        return ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .productId(productId)
            .build();
    }
}