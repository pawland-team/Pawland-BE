package com.pawland.review.respository;

import com.pawland.review.domain.OrderReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderReviewJpaRepository extends JpaRepository<OrderReview,Long> {
}
