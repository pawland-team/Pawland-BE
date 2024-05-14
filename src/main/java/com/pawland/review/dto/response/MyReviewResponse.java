package com.pawland.review.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewResponse {
    private String productImage;
    private String sellerId;
    private String sellerProfileImage;
    private Double star;
    private String content;
    private LocalDateTime createAt;

    private MyReviewResponse(String productImage, String sellerId, String sellerProfileImage, Double star, String content, LocalDateTime createAt) {
        this.productImage = productImage;
        this.sellerId = sellerId;
        this.sellerProfileImage = sellerProfileImage;
        this.star = star;
        this.content = content;
        this.createAt = createAt;
    }

    public static MyReviewResponse of(String productImage, String sellerId, String sellerProfileImage, Double star, String content, LocalDateTime createAt) {
        return new MyReviewResponse(productImage, sellerId, sellerProfileImage, star, content, createAt);
    }
}
