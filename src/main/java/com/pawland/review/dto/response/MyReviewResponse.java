package com.pawland.review.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewResponse {
    private String productImage;
    private String sellerId;
    private Double star;
    private String content;
    private LocalDateTime createAt;

    private MyReviewResponse(String productImage, String sellerId, Double star, String content, LocalDateTime createAt) {
        this.productImage = productImage;
        this.sellerId = sellerId;
        this.star = star;
        this.content = content;
        this.createAt = createAt;
    }

    public static MyReviewResponse of(String productImage, String sellerId, Double star, String content, LocalDateTime createAt) {
        return new MyReviewResponse(productImage, sellerId, star, content, createAt);
    }
}
