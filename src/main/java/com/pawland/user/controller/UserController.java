package com.pawland.user.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt-cookie")
public class UserController {

    private final UserService userService;

    @GetMapping("/my-info")
    public ResponseEntity getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInfoResponse userInfo = userService.getUserInfo(userPrincipal.getUsername());
        return ResponseEntity
            .status(OK)
            .body(userInfo);
    }

    @PatchMapping
    public ResponseEntity updateUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal, UserInfoUpdateRequest request) {
        userService.updateUser(userPrincipal.getUsername(), request);
        return ResponseEntity
            .status(OK)
            .body("프로필 수정에 성공했습니다.");
    }
}
