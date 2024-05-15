package com.pawland.review.respository;

import com.pawland.review.domain.OrderReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderReviewJpaRepository extends JpaRepository<OrderReview, Long> {
    Page<OrderReview> findAllByOrderSellerIdOrderByCreatedDateDesc(Long orderSellerId, Pageable pageable);
    List<OrderReview> findAllByOrderSellerIdOrOrderBuyerIdOrderByCreatedDateDesc(Long sellerId, Long buyerId);

}
