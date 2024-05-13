package com.pawland.review.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.review.dto.request.CreateReviewRequest;
import com.pawland.review.dto.response.MyReviewResponse;
import com.pawland.review.dto.response.OrderReviewResponse;
import com.pawland.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Tag(name = "ReviewController", description = "리뷰 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "주문 리뷰 작성")
    @PostMapping("/{orderId}")
    public ResponseEntity<OrderReviewResponse> createOrderReview(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId, @RequestBody CreateReviewRequest createReviewRequest) {
        return ResponseEntity.ok(reviewService.createReview(userPrincipal.getUserId(), orderId, createReviewRequest));
    }

    @Operation(summary = "나의 상품 리뷰 조회")
    @GetMapping("/my-review")
    public ResponseEntity<List<MyReviewResponse>> getMyReview(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(reviewService.getMyReview(userPrincipal.getUserId()));
    }

    @Operation(summary = "유저가 받은 리뷰 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<List<MyReviewResponse>> getReviewByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getMyReview(userId));
    }
}
