package com.pawland.chat.dto.response;

import com.pawland.chat.domain.ChatMessage;
import com.pawland.product.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Schema(name = "채팅방 목록 조회 시 개별 채팅방 정보")
public class ChatRoomInfoResponse {

    private Long roomId;
    private Long orderId;
    private UserInfo opponentUser;
    private ProductInfo productInfo;
    private LastMessage lastMessage;

    @Builder
    public ChatRoomInfoResponse(Long roomId, Long orderId, UserInfo opponentUser, ProductInfo productInfo) {
        this.roomId = roomId;
        this.orderId = orderId;
        this.opponentUser = opponentUser;
        this.productInfo = productInfo;
    }

    public ChatRoomInfoResponse of(ChatMessage chatMessage) {
        this.lastMessage = LastMessage.of(chatMessage);
        return this;
    }

    @Getter
    @Schema(name = "채팅 상대의 유저 정보")
    public static class UserInfo {
        private Long id;
        private String nickname;
        private String profileImage;

        public UserInfo(Long id, String nickname, String profileImage) {
            this.id = id;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }

    @Getter
    @Schema(name = "채팅방의 상품 정보")
    public static class ProductInfo {
        private Long id;
        private int price;
        private String productName;
        private String thumbnailImage;
        private String saleState;
        private Long purchaser;

        public ProductInfo(Long id, int price, String productName,
                           String thumbnailImage, Status saleState, Long purchaser) {
            this.id = id;
            this.price = price;
            this.productName = productName;
            this.thumbnailImage = thumbnailImage;
            this.saleState = saleState.getName();
            this.purchaser = purchaser;
        }
    }

    @Getter
    @Schema(name = "채팅 상대의 유저 정보")
    public static class LastMessage {
        private String messageId;
        private Long sender;
        private String message;
        private String messageTime;

        @Builder
        public LastMessage(String messageId, Long sender, String message, String messageTime) {
            this.messageId = messageId;
            this.sender = sender;
            this.message = message;
            this.messageTime = messageTime;
        }

        public static LastMessage of(ChatMessage chatMessage) {
            if (chatMessage == null) {
                return null;
            }
            return LastMessage.builder()
                .messageId(chatMessage.getId())
                .sender(chatMessage.getSenderId())
                .message(chatMessage.getMessage())
                .messageTime(chatMessage.getMessageTime().toString())
                .build();
        }
    }
}
