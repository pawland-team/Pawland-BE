package com.pawland.global.dto;

import lombok.Getter;

@Getter
public class ApiMessageResponse {

    private String message;

    public ApiMessageResponse(String message) {
        this.message = message;
    }
}
