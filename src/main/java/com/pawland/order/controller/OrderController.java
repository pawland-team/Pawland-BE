package com.pawland.order.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.order.dto.request.UpdateOrderRequest;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.order.service.OrderService;
import com.pawland.order.dto.request.OrderSearchCondition;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
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

    @Operation(summary = "주문 수정")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long orderId, @RequestBody UpdateOrderRequest updateOrderRequest) {
        return ResponseEntity.ok(orderService.updateOrder(userPrincipal.getUserId(), orderId, updateOrderRequest));
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
