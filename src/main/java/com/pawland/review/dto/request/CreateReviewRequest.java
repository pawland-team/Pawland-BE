package com.pawland.review.dto.request;

import lombok.Data;

@Data
public class CreateReviewRequest {
    private String content;
    private Double star;

    public CreateReviewRequest(String content, Double star) {
        this.content = content;
        this.star = star;
    }
}
