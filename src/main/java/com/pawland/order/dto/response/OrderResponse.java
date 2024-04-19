package com.pawland.order.dto.response;

import com.pawland.order.domain.Order;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.user.dto.UserResponse;
import lombok.Data;

@Data
public class OrderResponse {
    private Long id;
    private UserResponse seller;
    private UserResponse buyer;
    private ProductResponse product;

    private OrderResponse(Order order) {
        this.id = order.getId();
        this.seller = UserResponse.of(order.getSeller());
        this.buyer = UserResponse.of(order.getBuyer());
        this.product = ProductResponse.of(order.getProduct());
    }

    public static OrderResponse of(Order order) {
        return new OrderResponse(order);
    }
}
