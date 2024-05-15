package com.pawland.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "API 메시지 응답 값")
public class ApiMessageResponse {

    private String message;

    public ApiMessageResponse(String message) {
        this.message = message;
    }
}
