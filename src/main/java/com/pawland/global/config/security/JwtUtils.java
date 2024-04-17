package com.pawland.global.config.security;

import com.pawland.global.config.AppConfig;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Date;

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

    public String generateAccessToken(String name, Date dateTime) {
        SecretKey secretKey = getSecretKey();
        return Jwts.builder()
            .subject(name)
            .issuedAt(dateTime)
            .expiration(new Date(dateTime.getTime() + 36000L)) // 한달짜리 jwt
            .signWith(secretKey)
            .compact();
    }

    public String getJwtFromCookie(Cookie[] cookies) {
        if (cookies == null){
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
            throw new BadCredentialsException("JWT가 없습니다.");
        }
        try {
            Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt);
        } catch (JwtException e) {
            throw new BadCredentialsException("올바르지 않은 JWT 토큰 정보입니다.");
        }
    }
}
