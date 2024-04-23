package com.pawland.product.exception;

import lombok.Getter;

@Getter
public enum ProductExceptionMessage {
    PRODUCT_NOT_FOUND("상품을 찾을수 없습니다."),
    ACCESS_DENIED_EXCEPTION("변경 권한이 없습니다.");

    private final String message;

    ProductExceptionMessage(String message) {
        this.message = message;
    }
}
