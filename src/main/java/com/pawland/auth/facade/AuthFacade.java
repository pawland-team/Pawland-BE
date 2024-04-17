package com.pawland.auth.facade;

import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.mail.service.MailVerificationService;
import com.pawland.user.domain.User;
import com.pawland.user.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;
    private final MailVerificationService mailVerificationService;
    private final PasswordEncoder passwordEncoder;

    public void checkEmailDuplicate(String email) {
        userService.checkEmailDuplicate(email);
    }

    public void requestEmailVerification(String email) throws MessagingException, UnsupportedEncodingException {
        mailVerificationService.sendEmail(email);
    }

    public void signup(SignupRequest request) {
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .phoneNumber(request.getPhoneNumber())
            .build();
        userService.register(user);
    }
}
