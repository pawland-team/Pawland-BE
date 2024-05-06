package com.pawland.product.dto.response;

import com.pawland.post.domain.Region;
import com.pawland.product.domain.*;
import com.pawland.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "상품 응답")
public class ProductResponse {
    private Long id;
    private UserResponse seller;
    private Category category;
    private Species species;
    private Condition condition;
    private String name;
    private int price;
    private String content;
    private Region region;
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
