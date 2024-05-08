package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Condition {
    NEW("새상품"),USED("중고");

    private final String name;

    Condition(String name) {
        this.name = name;
    }

    public static Condition getInstance(String condition) {
        return Arrays.stream(Condition.values())
                .filter(c -> c.getName().equals(condition))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
