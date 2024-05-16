package com.pawland.chat.dto.request;

import com.pawland.chat.domain.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(name = "채팅방 생성 요청")
public class ChatRoomCreateRequest {

    @NotNull
    private Long sellerId;

    @NotNull
    private Long productId;

    @NotNull
    private Long orderId;

    @Builder
    public ChatRoomCreateRequest(Long sellerId, Long productId, Long orderId) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.orderId = orderId;
    }

    public ChatRoom toChatRoomWithMyId(Long buyerId) {
        return ChatRoom.builder()
            .buyerId(buyerId)
            .sellerId(sellerId)
            .productId(productId)
            .orderId(orderId)
            .build();
    }
}
