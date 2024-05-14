package com.pawland.product.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {
    private List<String> region;
    private List<String> species;
    private List<String> category;
    private String orderBy;
    private String content;
    private boolean isFree;
    private int page;
    private int size;

}
