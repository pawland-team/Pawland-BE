package com.pawland.global.exception;

import com.pawland.global.dto.ApiMessageResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(PawLandException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> PawLandExceptionHandler(PawLandException e) {

        return ResponseEntity
            .status(e.getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiMessageResponse(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(AccessDeniedException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "302", description = "이미 같은 리소스가 존재합니다.")
    public ResponseEntity<ApiMessageResponse> accessDeniedExceptionHandler(AccessDeniedException e) {
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiMessageResponse(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "400", description = "입력 값이 올바르지 않습니다.")
    public ResponseEntity<ApiMessageResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
        String[] errorMessages = e.getFieldErrors().stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .toArray(String[]::new);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiMessageResponse(errorMessages[0]));
    }

    // TODO: Enum 관련 커스텀 예외 추가 시 제거 예정
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "400", description = "입력 값이 올바르지 않습니다.")
    public ResponseEntity<ApiMessageResponse> invalidRequestHandler(IllegalArgumentException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiMessageResponse(e.getMessage()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(MailSendException.class)
    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponse(responseCode = "500",description = "메시지 전송 오류")
    public ResponseEntity<ApiMessageResponse> mailSendExceptionHandler(MailSendException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiMessageResponse(e.getMessage()));
    }
}
