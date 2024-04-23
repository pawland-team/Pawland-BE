package com.pawland.order.exception;

import com.pawland.global.exception.PawLandException;
import org.springframework.http.HttpStatus;

public class OrderException extends PawLandException {

    public OrderException(String message) {
        super(message);
    }

    public static class NotFoundOrder extends OrderException {
        public NotFoundOrder() {
            super(OrderExceptionMessage.ORDER_NOT_FOUND.getMessage());
        }
    }

    public static class AccessDeniedException extends OrderException {
        public AccessDeniedException() {
            super(OrderExceptionMessage.ACCESS_DENIED_EXCEPTION.getMessage());
        }
    }


    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
