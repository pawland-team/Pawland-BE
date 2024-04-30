package com.pawland.user.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.dto.response.UserInfoUpdateResponse;
import com.pawland.user.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
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

    @PatchMapping("/my-info")
    public ResponseEntity updateUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @Valid @RequestBody UserInfoUpdateRequest request) {
        UserInfoUpdateResponse updatedInfo = userService.updateUser(userPrincipal.getUsername(), request);
        return ResponseEntity
            .status(OK)
            .body(updatedInfo);
    }
}
