package com.pawland.post.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.dto.ApiMessageResponse;
import com.pawland.post.dto.request.PostWriteRequest;
import com.pawland.post.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt-cookie")
@Tag(name = "PostController", description = "커뮤니티 게시글 관련 컨트롤러 입니다.")
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiMessageResponse> writePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @Valid @RequestBody PostWriteRequest request) {
        postService.uploadPost(userPrincipal.getUserId(), request);
        return ResponseEntity
            .status(CREATED)
            .body(new ApiMessageResponse("게시글이 등록되었습니다."));
    }
}
