package com.pawland.auth.facade;

import com.pawland.auth.dto.request.SignupRequest;
import com.pawland.auth.dto.request.VerifyCodeRequest;
import com.pawland.auth.service.AuthService;
import com.pawland.global.config.security.JwtUtils;
import com.pawland.global.domain.DefaultImage;
import com.pawland.mail.service.MailVerificationService;
import com.pawland.user.domain.User;
import com.pawland.user.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AuthFacade {

    private final UserService userService;
    private final MailVerificationService mailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    public void checkNicknameDuplicate(String nickname) {
        userService.checkNicknameDuplicate(nickname);
    }

    public void checkEmailDuplicate(String email) {
        userService.checkEmailDuplicate(email);
    }

    public void sendVerificationCode(String email) throws MessagingException, UnsupportedEncodingException {
        mailVerificationService.sendVerificationCode(email);
    }

    public void verifyCode(VerifyCodeRequest request) {
        mailVerificationService.verifyCode(request.getEmail(), request.getCode());
    }

    public String signup(SignupRequest request) {
        mailVerificationService.checkEmailVerification(request.getEmail());
        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .profileImage(DefaultImage.getRandomProfileImage())
            .nickname(request.getNickname())
            .build();
        userService.register(user);
        return jwtUtils.generateJwtCookie(request.getEmail(), new Date());
    }

    public String oauth2Login(String code, String provider) {
        User user = authService.oauth2Login(code, provider);
        return jwtUtils.generateJwtCookie(user.getEmail(), new Date());
    }

    public String logout(String email) {
        return jwtUtils.expireJwtCookie(email);
    }
}
