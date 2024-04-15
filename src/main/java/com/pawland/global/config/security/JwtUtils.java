package com.pawland.global.config.security;

import com.pawland.global.config.AppConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final AppConfig appConfig;

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
}
