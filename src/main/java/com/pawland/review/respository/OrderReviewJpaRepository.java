package com.pawland.review.respository;

import com.pawland.review.domain.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderReviewJpaRepository extends JpaRepository<OrderReview, Long> {
    List<OrderReview> findByOrderSellerIdOrderByCreatedDateDesc(Long orderSellerId);

    List<OrderReview> findByOrderSellerEmailOrderByCreatedDateDesc(String email);
}
