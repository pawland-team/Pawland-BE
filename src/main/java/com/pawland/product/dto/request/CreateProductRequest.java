package com.pawland.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "상품 등록 요청")
public class CreateProductRequest {
    private String category;
    private String species;
    private String condition;
    @NotBlank
    private String name;
    private int price;
    @NotBlank
    private String content;
    @NotBlank
    private String region;
    private String thumbnailImage;
    private List<String> images;
}
