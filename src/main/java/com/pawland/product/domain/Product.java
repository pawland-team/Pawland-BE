package com.pawland.product.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Category category;
    @Enumerated(EnumType.STRING)
    private Species species;
    @Enumerated(EnumType.STRING)
    private Condition condition;

    private String name;

    private int price;

    private String content;

    private String region;

    private int view;
    @ManyToOne
    private User seller;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String thumbnailImageUrl;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Product(String category,String species,String condition,String name, int price, String content, String region,User seller) {
        this.category = Category.getInstance(category);
        this.species = Species.getInstance(species);
        this.condition = Condition.getInstance(condition);
        this.name = name;
        this.price = price;
        this.content = content;
        this.region = region;
        this.view = 0;
        this.seller = seller;
        this.status = Status.SELLING;
        //todo
        this.thumbnailImageUrl = "";
        this.imageUrls = new ArrayList<>();
    }

    public void update(UpdateProductRequest updateProductRequest) {
        if (updateProductRequest.getCategory() != null) {
            this.category = Category.getInstance(updateProductRequest.getCategory());
        }
        if (updateProductRequest.getSpecies() != null) {
            this.species = Species.getInstance(updateProductRequest.getSpecies());
        }
        if (updateProductRequest.getCondition() != null) {
            this.condition = Condition.getInstance(updateProductRequest.getCondition());
        }
        if (updateProductRequest.getName() != null) {
            this.name = updateProductRequest.getName();
        }
        if (updateProductRequest.getPrice() != 0) {
            this.price = updateProductRequest.getPrice();
        }
        if (updateProductRequest.getContent() != null) {
            this.content = updateProductRequest.getContent();
        }
        if (updateProductRequest.getRegion() != null) {
            this.region = updateProductRequest.getRegion();
        }
    }


}
