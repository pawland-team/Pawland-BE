package com.pawland.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pawland")
public class AppConfig {

    private final String jwtKey;
    private final String frontUrl;
    private final String defaultImage;
}
