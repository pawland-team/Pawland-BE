package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Species {
    DOG("강아지"),
    CAT("고양이"),
    ETC("그외 동물");

    private final String name;

    Species(String name) {
        this.name = name;
    }

    public static Species getInstance(String species) {
        return Arrays.stream(Species.values())
                .filter(s -> s.getName().equals(species))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
