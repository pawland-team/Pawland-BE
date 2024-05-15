package com.pawland.review.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReviewResponse {
    private String productImage;
    private Long reviewId;
    private Long reviewerId;
    private String reviewerNickName;
    private String reviewerProfileImage;
    private Double star;
    private String content;
    private LocalDateTime createAt;

    private MyReviewResponse(String productImage, Long reviewId, Long reviewerId, String sellerId, String sellerProfileImage, Double star, String content, LocalDateTime createAt) {
        this.productImage = productImage;
        this.reviewId = reviewId;
        this.reviewerId = reviewerId;
        this.reviewerNickName = sellerId;
        this.reviewerProfileImage = sellerProfileImage;
        this.star = star;
        this.content = content;
        this.createAt = createAt;
    }

    public static MyReviewResponse of(String productImage, Long reviewId, Long reviewerId, String sellerId, String sellerProfileImage, Double star, String content, LocalDateTime createAt) {
        return new MyReviewResponse(productImage, reviewId, reviewerId, sellerId, sellerProfileImage, star, content, createAt);
    }
}
