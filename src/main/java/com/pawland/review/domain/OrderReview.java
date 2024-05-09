package com.pawland.review.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.order.domain.Order;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderReview extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Order order;

    @ManyToOne(fetch =FetchType.LAZY)
    private User user;

    private String content;

    private Double star;

    public OrderReview(Order order, User user, String content, Double star) {
        this.order = order;
        this.user = user;
        this.content = content;
        this.star = star;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
