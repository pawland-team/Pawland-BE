package com.pawland.user.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.user.dto.request.UserInfoUpdateRequest;
import com.pawland.user.dto.response.UserInfoResponse;
import com.pawland.user.dto.response.UserInfoUpdateResponse;
import com.pawland.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt-cookie")
@Tag(name = "UserController", description = "유저 정보 관련 컨트롤러 입니다.")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "JWT로 내 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공")
    @ApiResponse(responseCode = "500", description = "삭제된 회원 또는 잘못된 JWT")
    @GetMapping(value = "/my-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoResponse> getUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserInfoResponse userInfo = userService.getUserInfo(userPrincipal.getUsername());
        return ResponseEntity
                .status(OK)
                .body(userInfo);
    }

    @Operation(summary = "내 정보 수정", description = "내 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "내 정보 수정 성공")
    @ApiResponse(responseCode = "400", description = "요청 값에 닉네임 누락 시")
    @ApiResponse(responseCode = "500", description = "삭제된 회원 또는 잘못된 JWT")
    @PutMapping(value = "/my-info", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoUpdateResponse> updateUserInfo(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                                 @Valid @RequestBody UserInfoUpdateRequest request) {
        UserInfoUpdateResponse updatedInfo = userService.updateUser(userPrincipal.getUsername(), request);
        return ResponseEntity
                .status(OK)
                .body(updatedInfo);
    }

    @Operation(summary = "유저 프로필 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUserProfile(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserInfoByUserId(userId));
    }

}
