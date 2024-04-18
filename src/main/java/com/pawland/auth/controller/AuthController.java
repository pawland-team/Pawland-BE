package com.pawland.auth.controller;

import com.pawland.auth.dto.request.EmailDupCheckRequest;
import com.pawland.auth.dto.request.VerifyCodeRequest;
import com.pawland.auth.dto.request.SendVerificationCodeRequest;
import com.pawland.auth.facade.AuthFacade;
import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.global.config.security.domain.LoginRequest;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @PostMapping(value = "/email-dupcheck", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity emailDupCheck(@Valid @RequestBody EmailDupCheckRequest request) {
        authFacade.checkEmailDuplicate(request.getEmail());
        return ResponseEntity
            .status(OK)
            .body("사용할 수 있는 이메일입니다.");
    }

    @PostMapping(value = "/send-verification-code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) throws MessagingException, UnsupportedEncodingException {
        authFacade.sendVerificationCode(request.getEmail());
        return ResponseEntity
            .status(CREATED)
            .body("인증 메일이 발송 되었습니다.");
    }

    @PostMapping(value = "/verify-code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        authFacade.verifyCode(request.getCode());
        return ResponseEntity
            .status(OK)
            .body("이메일 인증이 완료되었습니다.");
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signup(@Valid @RequestBody SignupRequest request) {
        authFacade.signup(request);
        return ResponseEntity
            .status(CREATED)
            .body("회원가입 되었습니다.");
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void login(@Valid @RequestBody LoginRequest request) {
        
    }
}
