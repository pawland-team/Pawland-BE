package com.pawland.global.config.security.filter;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String email;
    private String password;

    protected LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
