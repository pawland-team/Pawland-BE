package com.pawland.product.dto.response;

import com.pawland.product.domain.Product;
import com.pawland.product.domain.WishProduct;
import com.pawland.user.domain.User;
import com.pawland.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "상품 응답")
public class ProductResponse {
    private Long id;
    private UserResponse seller;
    private String category;
    private String species;
    private String condition;
    private String name;
    private int price;
    private String content;
    private String region;
    private int view;
    private String status;
    private String thumbnailImage;
    private List<String> imageUrls;
    private boolean isWished;
    private LocalDateTime createAt;

    public ProductResponse(Product product, User user) {
        this.id = product.getId();
        this.seller = UserResponse.of(product.getSeller());
        this.category = product.getCategory().getName();
        this.species = product.getSpecies().getName();
        this.condition = product.getCondition().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.content = product.getContent();
        this.region = product.getRegion().getName();
        this.view = product.getView();
        this.status = product.getStatus().getName();
        this.thumbnailImage = product.getThumbnailImageUrl();
        this.imageUrls = product.getImageUrls();
        this.isWished = product.getWishProducts().stream().map(WishProduct::getUser).toList().contains(user);
        this.createAt = product.getCreatedDate();
    }

    public static ProductResponse of(Product product,User user) {
        return new ProductResponse(product,user);
    }
}
