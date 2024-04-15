package com.pawland.auth.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.pawland.user.domain.User;
import com.pawland.user.service.UserService;
import com.pawland.auth.dto.request.SignupRequest;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;

    public void signup(SignupRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .password(request.getPassword())
            .phoneNumber(request.getPhoneNumber())
            .build();
        userService.register(user);
    }
}
