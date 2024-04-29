package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Species {
    DOG,
    CAT,
    ETC;

    public static Species getInstance(String species) {
        return Arrays.stream(Species.values())
                .filter(s -> s.name().equals(species))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
