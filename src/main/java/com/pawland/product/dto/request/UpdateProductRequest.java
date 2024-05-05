package com.pawland.product.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "상품 수정 요청")
public class UpdateProductRequest {
    private String category;
    private String species;
    private String condition;
    private String name;
    private int price;
    private String content;
    private String region;
    private MultipartFile thumbnailImage;
    private List<MultipartFile> images;
}
