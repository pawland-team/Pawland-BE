package com.pawland.order.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyOrderRequest {
    private String type;
    private int page;
    private int size;

    public MyOrderRequest(String type, int page, int size) {
        this.type = type;
        this.page = page;
        this.size = size;
    }
}
