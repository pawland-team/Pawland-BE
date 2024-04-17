package com.pawland.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "pawland")
public class AppConfig {

    private final String jwtKey;
    private final String frontUrl;

    public String getJwtKey() {
        return jwtKey;
    }

    public String getFrontUrl() {
        return frontUrl;
    }
}
