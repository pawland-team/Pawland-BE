package com.pawland.product.domain;

import com.pawland.global.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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

    @ElementCollection
    private List<String> imageUrls = new ArrayList<>();

}
