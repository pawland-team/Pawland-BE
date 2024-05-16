package com.pawland.order.service;

import com.pawland.order.domain.Order;
import com.pawland.order.domain.OrderStatus;
import com.pawland.order.dto.request.MyOrderRequest;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.order.exception.OrderException;
import com.pawland.order.respository.OrderJpaRepository;
import com.pawland.product.domain.Product;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderJpaRepository orderJpaRepository;
    private final UserRepository userRepository;
    private final ProductJpaRepository productJpaRepository;

    @Transactional
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
    public boolean doneOrder(Long userId, Long orderId) {
        if (isSeller(userId, orderId)) {
            Order order = getOrderById(orderId);
            order.setSellerCheck(true);
            return true;
        } else if (isBuyer(userId, orderId)) {
            Order order = getOrderById(orderId);
            order.setBuyerCheck(true);
            return true;
        } else {
            throw new OrderException.AccessDeniedException();
        }

    }

    @Transactional
    public Boolean cancelOrder(Long userId, Long orderId) {
        if (isSeller(userId, orderId) || isBuyer(userId, orderId)) {
            Order order = getOrderById(orderId);

            order.changeStatus(OrderStatus.CANCEL);

            return true;
        } else {
            throw new OrderException.AccessDeniedException();
        }
    }

    public Page<OrderResponse> getMyOrder(Long userId, MyOrderRequest myOrderRequest) {
        Pageable pageable = PageRequest.of(myOrderRequest.getPage() - 1, myOrderRequest.getSize());
        if (myOrderRequest.getType() == null) {
            return orderJpaRepository.findBySellerIdOrBuyerIdOrderByCreatedDate(userId, userId,pageable).map(OrderResponse::of);
        } else if (myOrderRequest.getType().equals("판매내역")) {
            return orderJpaRepository.findBySellerIdOrderByCreatedDate(userId,pageable).map(OrderResponse::of);
        } else if (myOrderRequest.getType().equals("구매내역")) {
            return orderJpaRepository.findByBuyerIdOrderByCreatedDate(userId,pageable).map(OrderResponse::of);
        }

        return null;
    }

    private User getUserById(Long buyerId) {
        return userRepository.findById(buyerId).orElseThrow(UserException.NotFoundUser::new);
    }

    private Product getProductById(Long productId) {
        return productJpaRepository.findById(productId).orElseThrow(ProductException.NotFoundProduct::new);
    }

    private Order getOrderById(Long orderId) {
        return orderJpaRepository.findById(orderId).orElseThrow(OrderException.NotFoundOrder::new);
    }

    private boolean isSeller(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        return order.getSeller().getId().equals(userId);
    }

    private boolean isBuyer(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        return order.getBuyer().getId().equals(userId);
    }
}
