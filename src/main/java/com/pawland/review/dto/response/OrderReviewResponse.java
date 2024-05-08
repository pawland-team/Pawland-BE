package com.pawland.review.dto.response;

import com.pawland.review.domain.OrderReview;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderReviewResponse {
    private Long id;
    private String content;
    private Double star;
    private LocalDateTime createdAt;


    private OrderReviewResponse(Long id, String content, Double star,LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.star = star;
        this.createdAt = createdAt;
    }

    public static OrderReviewResponse of(OrderReview orderReview) {
        return new OrderReviewResponse(orderReview.getId(), orderReview.getContent(), orderReview.getStar(),orderReview.getCreatedDate());
    }
}
