package com.pawland.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pawland.auth.facade.AuthFacade;
import com.pawland.auth.dto.request.SignupRequest;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthFacade authFacade;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        request.encodePassword(passwordEncoder);
        authFacade.signup(request);
    }
}
