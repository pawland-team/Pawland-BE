package com.pawland.order.dto.response;

import com.pawland.order.domain.Order;
import com.pawland.order.domain.OrderStatus;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.review.dto.response.OrderReviewResponse;
import com.pawland.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "주문 응답")
public class OrderResponse {
    private Long id;
    private UserResponse seller;
    private UserResponse buyer;
    private ProductResponse product;
    private boolean sellerCheck;
    private boolean buyerCheck;
    private OrderStatus orderStatus;
    private OrderReviewResponse orderReviewResponse;

    private OrderResponse(Order order) {
        this.id = order.getId();
        this.seller = UserResponse.of(order.getSeller());
        this.buyer = UserResponse.of(order.getBuyer());
        this.product = ProductResponse.of(order.getProduct(), null);
        this.sellerCheck = order.isSellerCheck();
        this.buyerCheck = order.isBuyerCheck();
        this.orderStatus = order.getStatus();
        this.orderReviewResponse = order.getOrderReview() != null ? OrderReviewResponse.of(order.getOrderReview()) : null;
    }

    public static OrderResponse of(Order order) {
        return new OrderResponse(order);
    }
}
