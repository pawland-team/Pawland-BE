package com.pawland.auth.controller;

import com.pawland.auth.dto.request.*;
import com.pawland.auth.facade.AuthFacade;
import com.pawland.global.config.AppConfig;
import com.pawland.global.config.security.domain.LoginRequest;
import com.pawland.global.dto.ApiMessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "인증 관련 컨트롤러 입니다.")
public class AuthController {

    private final AuthFacade authFacade;
    private final AppConfig appConfig;

    @Operation(summary = "닉네임 중복 확인", description = "요청한 닉네임이 이미 가입된 닉네임인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "사용할 수 있는 닉네임")
    @ApiResponse(responseCode = "400", description = "사용중인 닉네임")
    @PostMapping(value = "/nickname-dupcheck", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> nicknameDupCheck(@Valid @RequestBody NicknameDupCheckRequest request) {
        authFacade.checkNicknameDuplicate(request.getNickname());
        return ResponseEntity
            .status(OK)
            .body(new ApiMessageResponse("사용할 수 있는 닉네임입니다."));
    }

    @Operation(summary = "이메일 중복 확인", description = "요청한 이메일이 이미 가입된 이메일인지 확인합니다.")
    @ApiResponse(responseCode = "200", description = "사용할 수 있는 이메일")
    @ApiResponse(responseCode = "400", description = "사용중인 이메일")
    @PostMapping(value = "/email-dupcheck", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> emailDupCheck(@Valid @RequestBody EmailDupCheckRequest request) {
        authFacade.checkEmailDuplicate(request.getEmail());
        return ResponseEntity
            .status(OK)
            .body(new ApiMessageResponse("사용할 수 있는 이메일입니다."));
    }

    @Operation(summary = "이메일로 인증번호 요청", description = "요청한 메일 주소로 인증번호가 담긴 메일을 발송 합니다.")
    @ApiResponse(responseCode = "201", description = "인증번호 요청 성공")
    @ApiResponse(responseCode = "500", description = "메일 전송 실패")
    @PostMapping(value = "/send-verification-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) throws MessagingException, UnsupportedEncodingException {
        authFacade.sendVerificationCode(request.getEmail());
        return ResponseEntity
            .status(CREATED)
            .body(new ApiMessageResponse("인증 메일이 발송 되었습니다."));
    }

    @Operation(summary = "발급된 인증번호로 이메일 인증", description = "이메일과 인증번호를 확인하여 이메일을 인증합니다.")
    @ApiResponse(responseCode = "200", description = "메일 인증 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 인증번호")
    @PostMapping(value = "/verify-code", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> verifyCode(@Valid @RequestBody VerifyCodeRequest request) {
        authFacade.verifyCode(request);
        return ResponseEntity
            .status(OK)
            .body(new ApiMessageResponse("이메일 인증이 완료되었습니다."));
    }

    @Operation(summary = "회원가입", description = "회원가입 성공 시 쿠키를 반환합니다.")
    @ApiResponse(responseCode = "201", description = "회원가입 성공",
        headers = {
            @Header(name = "Set-Cookie", description = "인증 쿠키")
        })
    @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> signup(@Valid @RequestBody SignupRequest request) {
        String jwtCookie = authFacade.signup(request);

        return ResponseEntity
            .status(CREATED)
            .header(HttpHeaders.SET_COOKIE, jwtCookie)
            .body(new ApiMessageResponse("회원가입 되었습니다."));
    }

    @Operation(summary = "로그인", description = "로그인 성공 시 쿠키를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공",
        headers = {
            @Header(name = "Set-Cookie", description = "인증 쿠키")
        })
    @ApiResponse(responseCode = "400", description = "잘못된 아이디 혹은 비밀번호")
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(new ApiMessageResponse(""));
    }

    @Operation(summary = "소셜 로그인", description = "소셜 로그인 성공 시 쿠키를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "로그인에 성공",
        headers = {
            @Header(name = "Set-Cookie", description = "인증 쿠키")
        })
    @ApiResponse(responseCode = "400", description = "잘못된 아이디 혹은 비밀번호")
    @GetMapping("/oauth2/{provider}")
    public void oauth2Login(@PathVariable String provider, @RequestParam String code, HttpServletResponse response) throws IOException {
        String jwtCookie = authFacade.oauth2Login(code, provider);
        response.setHeader(HttpHeaders.SET_COOKIE, jwtCookie);
        response.sendRedirect(appConfig.getFrontTestUrl());
    }
}
