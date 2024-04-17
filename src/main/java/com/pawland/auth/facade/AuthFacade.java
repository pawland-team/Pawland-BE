package com.pawland.auth.facade;

import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.user.domain.User;
import com.pawland.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .build();
        userService.register(user);
    }
}
