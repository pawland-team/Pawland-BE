package com.pawland.chat.dto.response;

import com.pawland.product.domain.Status;
import lombok.*;

@Getter
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
