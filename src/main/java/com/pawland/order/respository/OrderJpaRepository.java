package com.pawland.order.respository;

import com.pawland.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<Order,Long> {
}
