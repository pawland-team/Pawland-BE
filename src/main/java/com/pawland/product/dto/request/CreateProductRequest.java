package com.pawland.product.dto.request;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CreateProductRequest {
    // 이름
    private String name;
    // 가격
    private int price;
    // 설명
    private String content;
    // 지역
    private String region;
    // 카테고리
    private String category;
    // 사진
    private List<MultipartFile> images;
}
