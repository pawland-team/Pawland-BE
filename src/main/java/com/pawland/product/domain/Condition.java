package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Condition {
    NEW,USED;

    public static Condition getInstance(String condition) {
        return Arrays.stream(Condition.values())
                .filter(c -> c.name().equals(condition))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
