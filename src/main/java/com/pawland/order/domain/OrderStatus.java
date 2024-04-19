package com.pawland.order.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OrderStatus {
    DONE("완료"),
    PROCEEDING("진행중");

    private final String name;

    OrderStatus(String name) {
        this.name = name;
    }

    public static OrderStatus getInstance(String status) {
        return Arrays.stream(OrderStatus.values())
                .filter(orderStatus -> orderStatus.getName().equals(status))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
