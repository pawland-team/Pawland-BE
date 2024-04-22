package com.pawland.order.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
@Tag(name = "OrderController", description = "주문 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;


    @Operation(summary = "주문")
    @PostMapping("/{productId}")
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(userPrincipal.getUserId(), productId));
    }

    @Operation(summary = "주문 조회")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOneOrderById(orderId));
    }

    @Operation(summary = "거래 완료")
    @PutMapping("/done/{orderId}")
    public ResponseEntity<Boolean> doneOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.doneOrder(userPrincipal.getUserId(), orderId));
    }

    @Operation(summary = "거래 취소")
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Boolean> cancelOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userPrincipal.getUserId(), orderId));
    }
}
