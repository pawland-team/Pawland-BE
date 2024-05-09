package com.pawland.chat.domain;

import com.pawland.global.domain.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long buyerId;

    @NotNull
    private Long sellerId;

    @NotNull
    private Long productId;

    @Builder
    public ChatRoom(Long id, Long sellerId, Long buyerId, Long productId) {
        this.id = id;
        this.sellerId = sellerId;
        this.buyerId = buyerId;
        this.productId = productId;
    }
}
