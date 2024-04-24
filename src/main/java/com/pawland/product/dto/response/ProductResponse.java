package com.pawland.product.dto.response;

import com.pawland.product.domain.*;
import com.pawland.user.dto.UserResponse;
import lombok.Data;

import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private UserResponse seller;
    private Category category;
    private Species species;
    private Condition condition;
    private String name;
    private int price;
    private String content;
    private String region;
    private int view;
    private Status status;
    private String thumbnailUrl;
    private List<String> imageUrls;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.seller = UserResponse.of(product.getSeller());
        this.category = product.getCategory();
        this.species = product.getSpecies();
        this.condition = product.getCondition();
        this.name = product.getName();
        this.price = product.getPrice();
        this.content = product.getContent();
        this.region = product.getRegion();
        this.view = product.getView();
        this.status = product.getStatus();
        this.thumbnailUrl = product.getThumbnailImageUrl();
        this.imageUrls = product.getImageUrls();
    }

    public static ProductResponse of(Product product) {
        return new ProductResponse(product);
    }
}
