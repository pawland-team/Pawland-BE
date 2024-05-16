package com.pawland.global.config.security;

import com.pawland.global.config.AppConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final AppConfig appConfig;
    private final UserDetailsService userDetailsService;
    private static final String JWT_NAME = "jwt";

    public SecretKey getSecretKey() {
        byte[] byteJwtKey = Decoders.BASE64.decode(appConfig.getJwtKey());
        return Keys.hmacShaKeyFor(byteJwtKey);
    }

    public String generateJwtCookie(String email, Date dateTime) {
        SecretKey secretKey = getSecretKey();
        String jwt = Jwts.builder()
            .subject(email)
            .issuedAt(dateTime)
            .expiration(new Date(dateTime.getTime() + 24L * 60 * 60 * 1000)) // 하루짜리 jwt
            .signWith(secretKey)
            .compact();
        return createCookie(jwt).toString();
    }

    public String expireJwtCookie(String email) {
        SecretKey secretKey = getSecretKey();
        String jwt = Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date())
            .signWith(secretKey)
            .compact();
        return createCookie(jwt).toString();
    }

    public String getJwtFromCookie(Cookie[] cookies) {
        if (cookies == null){
            log.error("[쿠키가 없음]");
            throw new BadCredentialsException("JWT가 없습니다."); // TODO: 예외 메시지 Enum 만들기
        }
        return Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(JWT_NAME))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }

    public Authentication getAuthentication(String jwt) {
        validateJwt(jwt);
        String email = Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(jwt)
            .getPayload()
            .getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private void validateJwt(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            log.error("[JWT가 없음]");
            throw new BadCredentialsException("JWT가 없습니다.");
        }
        try {
            Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt);
        } catch (JwtException e) {
            log.error("[만료된 JWT]");
            throw new BadCredentialsException("올바르지 않은 JWT 토큰 정보입니다.");
        }
    }

    private ResponseCookie createCookie(String jwt) {
        return ResponseCookie.from(JWT_NAME, jwt)
            .domain(appConfig.getBackDomain())
            .path("/")
            .secure(true)
            .httpOnly(true)
            .maxAge(Duration.ofDays(30))
            .sameSite("None")
            .build();
    }
}
