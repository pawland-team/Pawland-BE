package com.pawland.order.exception;

import lombok.Getter;

@Getter
public enum OrderExceptionMessage {

    ORDER_NOT_FOUND("상품을 찾을수 없습니다."),
    ACCESS_DENIED_EXCEPTION("변경 권한이 없습니다."),
    ALREADY_EXISTS_ORDER("이미 주문이 있습니다.");

    private final String message;

    OrderExceptionMessage(String message) {
        this.message = message;
    }
}
