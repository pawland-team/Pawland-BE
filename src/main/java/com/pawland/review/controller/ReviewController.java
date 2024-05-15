package com.pawland.review.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.config.swagger.SecurityNotRequired;
import com.pawland.review.dto.request.CreateReviewRequest;
import com.pawland.review.dto.response.MyReviewResponse;
import com.pawland.review.dto.response.OrderReviewResponse;
import com.pawland.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review")
@Tag(name = "ReviewController", description = "리뷰 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt-cookie")
public class ReviewController {

    private final ReviewService reviewService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "주문 리뷰 작성")
    @PostMapping("/{orderId}")
    public ResponseEntity<OrderReviewResponse> createOrderReview(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId, @RequestBody CreateReviewRequest createReviewRequest) {
        return ResponseEntity.ok(reviewService.createReview(userPrincipal.getUserId(), orderId, createReviewRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "나의 상품 리뷰 조회")
    @GetMapping("/my-review")
    public ResponseEntity<Page<MyReviewResponse>> getMyReview(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(required = true) int page, @RequestParam(required = true) int size) {
        return ResponseEntity.ok(reviewService.getMyReview(userPrincipal.getUserId(), page, size));
    }

    @SecurityNotRequired
    @Operation(summary = "유저가 받은 리뷰 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<Page<MyReviewResponse>> getReviewByUser(@PathVariable Long userId, @RequestParam(required = true) int page, @RequestParam(required = true) int size) {
        return ResponseEntity.ok(reviewService.getMyReview(userId,page,size));
    }
}
