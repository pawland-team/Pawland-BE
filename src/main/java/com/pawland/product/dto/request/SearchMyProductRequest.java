package com.pawland.product.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchMyProductRequest {
    private String type;
    private int page;
    private int size;

    public SearchMyProductRequest(String type, int page, int size) {
        this.type = type;
        this.page = page;
        this.size = size;
    }
}
