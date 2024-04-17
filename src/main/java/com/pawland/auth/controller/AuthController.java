package com.pawland.auth.controller;

import com.pawland.auth.dto.request.emailDupCheckRequest;
import com.pawland.auth.dto.request.VerifyEmailReqeust;
import com.pawland.auth.facade.AuthFacade;
import com.pawland.auth.dto.request.SignupRequest;
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
    public ResponseEntity emailDupCheck(@Valid @RequestBody emailDupCheckRequest request) {
        authFacade.checkEmailDuplicate(request.getEmail());
        return ResponseEntity
            .status(OK)
            .body("사용할 수 있는 이메일입니다.");
    }

    @PostMapping(value = "/email-verification-request", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity requestEmailVerification(@Valid @RequestBody VerifyEmailReqeust request) throws MessagingException, UnsupportedEncodingException {
        authFacade.requestEmailVerification(request.getEmail());
        return ResponseEntity
            .status(CREATED)
            .body("인증 메일이 발송 되었습니다.");
    }

    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signup(@Valid @RequestBody SignupRequest request) {
        authFacade.signup(request);
        return ResponseEntity
            .status(CREATED)
            .body("회원가입 되었습니다.");
    }
}
