package com.pawland.order.respository;

import com.pawland.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order,Long> {
    Page<Order> findBySellerIdOrderByCreatedDate(Long sellerId,Pageable pagable);
    Page<Order> findByBuyerIdOrderByCreatedDate(Long buyerId,Pageable pagable);
    Page<Order> findBySellerIdOrBuyerIdOrderByCreatedDate(Long sellerId, Long buyerId, Pageable pageable);
}
