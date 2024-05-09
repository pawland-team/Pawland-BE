package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Status {
    DONE("판매 완료"),
    SELLING("판매중");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public static Status getInstance(String name) {
        return Arrays.stream(Status.values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
