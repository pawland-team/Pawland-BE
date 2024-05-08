package com.pawland.chat.dto.response;

import com.pawland.product.domain.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Schema(name = "채팅방 목록 조회 시 개별 채팅방 정보")
public class ChatRoomInfoResponse {

    private Long roomId;
    private UserInfo opponentUser;
    private ProductInfo productInfo;

    public ChatRoomInfoResponse(Long roomId, UserInfo opponentUser, ProductInfo productInfo) {
        this.roomId = roomId;
        this.opponentUser = opponentUser;
        this.productInfo = productInfo;
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
        private String imageThumbnail;
        private Status saleState;
        private Long purchaser;

        public ProductInfo(Long id, int price, String productName,
                           String imageThumbnail, Status saleState, Long purchaser) {
            this.id = id;
            this.price = price;
            this.productName = productName;
            this.imageThumbnail = imageThumbnail;
            this.saleState = saleState;
            this.purchaser = purchaser;
        }
    }
}
