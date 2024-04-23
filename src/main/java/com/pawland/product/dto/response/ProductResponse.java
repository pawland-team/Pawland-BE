package com.pawland.product.dto.response;

import com.pawland.product.domain.Category;
import com.pawland.product.domain.Product;
import com.pawland.user.dto.UserResponse;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private UserResponse seller;
    private Category category;
    private String name;
    private int price;
    private String content;
    private String region;
    private int view;
    private List<String> imageUrls;

    private ProductResponse(Long id,UserResponse seller, Category category, String name, int price, String content, String region, int view, List<String> imageUrls) {
        this.id = id;
        this.seller = seller;
        this.category = category;
        this.name = name;
        this.price = price;
        this.content = content;
        this.region = region;
        this.view = view;
        this.imageUrls = imageUrls;
    }

    public static ProductResponse of(Product product) {
        return new ProductResponse(product.getId(), UserResponse.of(product.getSeller()), product.getCategory(), product.getName(), product.getPrice(), product.getContent(), product.getRegion(), product.getView(), product.getImageUrls());
    }
}
