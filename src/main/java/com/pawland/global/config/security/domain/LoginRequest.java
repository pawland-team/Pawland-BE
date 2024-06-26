package com.pawland.global.config.security.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;

    @Builder
    protected LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
