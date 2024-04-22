package com.pawland.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotBlank
    private String name;
    @NotBlank
    private int price;
    @NotBlank
    private String content;
    @NotBlank
    private String region;
    @NotBlank
    private String category;
    private List<MultipartFile> images;
}
