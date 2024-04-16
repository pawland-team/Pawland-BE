package com.pawland.product.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdateProductRequest {
    private String name;
    private int price;
    private String content;
    private String region;
    private String category;
    private List<MultipartFile> images;
}
