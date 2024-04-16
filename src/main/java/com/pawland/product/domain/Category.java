package com.pawland.product.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Category {
    FOOD("사료"),
    TOY("장난감"),
    CLOTHING("옷"),
    ACCESSORIES("용품"),
    LITTER_AND_WASTE_MANAGEMENT("배변용품"),
    HYGIENE_PRODUCTS("위생용품"),
    HEALTHCARE_PRODUCTS("건강 관리용품"),
    GROOMING_AND_CLEANLINESS_PRODUCTS("청결 용품"),
    TRAINING_AIDS("훈련용품"),
    PROTECTIVE_GEAR("보호장비"),
    TRAVEL_ACCESSORIES("여행용품"),
    HOME_DECOR("홈 장식"),
    CARRIERS_AND_CAGES("캐리어 및 케이지"),
    PARTY_SUPPLIES("파티용품"),
    PORTABLE_ITEMS("휴대용 용품");

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
