package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
    FOOD("사료"),
    TOY("장난감"),
    CLOTHING("옷"),
    ACCESSORY("악세사리"),
    ETC("그 외 상품");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public static Category getInstance(String category) {
        return Arrays.stream(Category.values()).
                filter(c -> c.getName().equals(category))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
