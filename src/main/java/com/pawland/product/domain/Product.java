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
    // 상품카테고리
    private Category category;
    // 상품이름
    private String name;
    // 상품가격
    private int price;
    // 상품설명
    private String content;
    // 지역 (주소 api 를 사용할것인가 ?)
    private String region;
    // private Users seller;
    private int view;
    @ManyToOne
    private User seller;

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

    @Builder
    public Product(Category category, String name, int price, String content, String region, int view, List<String> imageUrls,User user) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.content = content;
        this.region = region;
        this.view = view;
        this.imageUrls = imageUrls;
        this.seller = user;
    }

    public void update(UpdateProductRequest updateProductRequest) {

    }
}
