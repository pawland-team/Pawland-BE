package com.pawland.order.respository;

import com.pawland.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order,Long> {
    List<Order> findBySellerIdOrderByCreatedDate(Long sellerId);
    List<Order> findByBuyerIdOrderByCreatedDate(Long buyerId);
    List<Order> findBySellerIdOrBuyerIdOrderByCreatedDate(Long sellerId, Long buyerId);
}
