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

    private Category category;

    private String name;

    private int price;

    private String content;

    private String region;

    private int view;
    @ManyToOne
    private User seller;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Product(Category category, String name, int price, String content, String region, int view, List<String> imageUrls,User seller) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.content = content;
        this.region = region;
        this.view = view;
        this.imageUrls = imageUrls;
        this.seller = seller;
        this.status = Status.SELLING;
    }

    public void update(UpdateProductRequest updateProductRequest) {
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
        if (updateProductRequest.getImages() != null && !updateProductRequest.getImages().isEmpty()) {
            this.imageUrls.clear();
        }
    }
}
