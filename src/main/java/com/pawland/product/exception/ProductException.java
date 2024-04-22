package com.pawland.product.exception;

import com.pawland.global.exception.PawLandException;
import org.springframework.http.HttpStatus;

public class ProductException extends PawLandException {

    public ProductException(String message) {
        super(message);
    }

    public static class NotFoundProduct extends ProductException {
        public NotFoundProduct() {
            super(ProductExceptionMessage.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    public static class AccessDeniedException extends ProductException {
        public AccessDeniedException() {
            super(ProductExceptionMessage.ACCESS_DENIED_EXCEPTION.getMessage());
        }
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
