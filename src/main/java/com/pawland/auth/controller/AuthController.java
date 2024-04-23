package com.pawland.auth.controller;

import com.pawland.auth.dto.request.EmailDupCheckRequest;
import com.pawland.auth.dto.request.VerifyCodeRequest;
import com.pawland.auth.dto.request.SendVerificationCodeRequest;
import com.pawland.auth.facade.AuthFacade;
import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.global.config.security.domain.LoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import java.time.Duration;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacade authFacade;

    @Operation(summary = "이메일 중복 확인", description = "요청한 이메일이 이미 가입된 이메일인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "사용할 수 있는 이메일")
    @ApiResponse(responseCode = "400", description = "사용중인 이메일")
    @PostMapping(value = "/email-dupcheck", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity emailDupCheck(@Valid @RequestBody EmailDupCheckRequest request) {
        authFacade.checkEmailDuplicate(request.getEmail());
        return ResponseEntity
            .status(OK)
            .body("사용할 수 있는 이메일입니다.");
    }

    @Operation(summary = "이메일로 인증번호 요청", description = "요청한 메일 주소로 인증번호가 담긴 메일을 발송 합니다.")
    @ApiResponse(responseCode = "201", description = "인증번호 요청 성공")
    @ApiResponse(responseCode = "500", description = "메일 전송 실패")
    @PostMapping(value = "/send-verification-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) throws MessagingException, UnsupportedEncodingException {
        authFacade.sendVerificationCode(request.getEmail());
        return ResponseEntity
            .status(CREATED)
            .body("인증 메일이 발송 되었습니다.");
    }

    @Operation(summary = "발급된 인증번호로 이메일 인증", description = "이메일과 인증번호를 확인하여 이메일을 인증합니다.")
    @ApiResponse(responseCode = "200", description = "메일 인증 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 인증번호")
    @PostMapping(value = "/verify-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        authFacade.verifyCode(request);
        return ResponseEntity
            .status(OK)
            .body("이메일 인증이 완료되었습니다.");
    }

    @Operation(summary = "회원가입", description = "회원가입 성공 시 쿠키를 반환합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
        headers = {
            @Header(name = "Set-Cookie", description = "인증 쿠키")
        })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity signup(@Valid @RequestBody SignupRequest request) {
        String jwt = authFacade.signup(request);

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
            .domain("localhost")   // todo 서버 환경에 따라 설정파일로 분리해서 관리
            .path("/")
            .secure(false)
            .maxAge(Duration.ofDays(30))  // 한달이 국룰
            .sameSite("Strict")
            .build();

        return ResponseEntity
            .status(CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body("회원가입 되었습니다.");
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 쿠키를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인에 성공",
        headers = {
            @Header(name = "Set-Cookie", description = "인증 쿠키")
        })
    @ApiResponse(responseCode = "400", description = "잘못된 아이디 혹은 비밀번호")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public void login(@Valid @RequestBody LoginRequest request) {

    }
}
