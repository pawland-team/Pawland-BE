package com.pawland.global.config.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawland.global.config.security.JwtUtils;
import com.pawland.global.config.security.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtUtils.generateAccessToken(principal.getUsername(), new Date());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
            .domain("localhost")   // todo 서버 환경에 따라 설정파일로 분리해서 관리
            .path("/")
            .secure(false)
            .maxAge(Duration.ofDays(30))
            .sameSite("Strict")
            .build();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());
        response.setStatus(SC_OK);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        objectMapper.writeValue(response.getWriter(), "로그인에 성공했습니다.");
    }
}
