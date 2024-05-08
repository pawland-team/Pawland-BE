package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Species {
    DOG("강아지"),
    CAT("고양이"),
    ETC("그외");

    private final String species;

    Species(String species) {
        this.species = species;
    }

    public static Species getInstance(String species) {
        return Arrays.stream(Species.values())
                .filter(s -> s.getSpecies().equals(species))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
