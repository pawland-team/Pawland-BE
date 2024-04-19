package com.pawland.order.service;

import com.pawland.order.domain.Order;
import com.pawland.order.domain.OrderStatus;
import com.pawland.order.dto.request.UpdateOrderRequest;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.product.domain.Product;
import com.pawland.order.dto.request.OrderSearchCondition;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final UserRepository userRepository;
    private final ProductJpaRepository productJpaRepository;

    public OrderResponse createOrder(Long buyerId, Long productId) {
        User buyer = getUserById(buyerId);
        Product product = getProductById(productId);

        Order order = new Order(product.getSeller(), buyer, product);

        orderJpaRepository.save(order);

        return OrderResponse.of(order);
    }

    public OrderResponse getOneOrderById(Long orderId) {
        Order order = getOrderById(orderId);
        return OrderResponse.of(order);
    }

    @Transactional
    public OrderResponse updateOrder(Long userId, Long orderId, UpdateOrderRequest updateOrderRequest) {
        canUpdate(userId, orderId);
        Order order = getOrderById(orderId);

        OrderStatus orderStatus = OrderStatus.getInstance(updateOrderRequest.getStatus());

        order.changeStatus(orderStatus);

        return OrderResponse.of(order);
    }

    private User getUserById(Long buyerId) {
        return userRepository.findById(buyerId).orElseThrow(IllegalArgumentException::new);
    }

    private Product getProductById(Long productId) {
        return productJpaRepository.findById(productId).orElseThrow(IllegalArgumentException::new);
    }

    private Order getOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(IllegalArgumentException::new);
    }

    private boolean canUpdate(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getSeller().getId().equals(userId)) {
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public boolean finishedOrder(Long userId, Long orderId) {
        canUpdate(userId, orderId);

        Order order = getOrderById(orderId);

        order.changeStatus(OrderStatus.DONE);

        Product product = order.getProduct();

        product.sold();

        return true;
    }
}
