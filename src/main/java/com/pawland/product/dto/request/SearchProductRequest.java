package com.pawland.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {
    private String region;
    private String species;
    private String category;
    private String orderBy;
    private String content;
    private boolean isFree;
    private int page;
    private int size;

}
