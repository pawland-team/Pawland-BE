package com.pawland.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class WebSocketExceptionHandler {

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleWebSocketException(MethodArgumentNotValidException e) {
        String[] errorMessages = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .toArray(String[]::new);
        log.info("[예외처리] = {}", errorMessages[0]);
    }
}
