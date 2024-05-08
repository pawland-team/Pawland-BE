package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Condition {
    NEW("새상품"),USED("중고");

    private final String condition;

    Condition(String condition) {
        this.condition = condition;
    }

    public static Condition getInstance(String condition) {
        return Arrays.stream(Condition.values())
                .filter(c -> c.getCondition().equals(condition))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
