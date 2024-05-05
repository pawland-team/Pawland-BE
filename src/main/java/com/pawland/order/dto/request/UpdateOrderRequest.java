package com.pawland.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "주문 수정 요청")
public class UpdateOrderRequest {
    private String status;
}
