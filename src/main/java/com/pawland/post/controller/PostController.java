package com.pawland.post.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.dto.ApiMessageResponse;
import com.pawland.post.dto.request.PostCreateRequest;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.post.dto.request.UpdatePostRequest;
import com.pawland.post.dto.response.PostResponse;
import com.pawland.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwt-cookie")
@Tag(name = "PostController", description = "커뮤니티 게시글 관련 컨트롤러 입니다.")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 등록 성공")
    @ApiResponse(responseCode = "400", description = "제목 누락 시")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiMessageResponse> writePost(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                        @Valid @RequestBody PostCreateRequest request) {
        postService.uploadPost(userPrincipal.getUserId(), request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiMessageResponse("게시글이 등록되었습니다."));
    }

    @Operation(summary = "게시글 조회", description = "게시글을 조회 합니다")
    @ApiResponse(responseCode = "201", description = "게시글 조회 성공")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PostResponse>> getPosts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestParam(required = true, defaultValue = "1") int page,
                                                       @RequestParam(required = false) String content,
                                                       @RequestParam(required = false) List<String> region,
                                                       @RequestParam(required = false) String orderBy) {
        Long userId = Optional.ofNullable(userPrincipal).map(UserPrincipal::getUserId).orElse(null);
        return ResponseEntity.ok(postService.getPosts(userId, PostSearchRequest.builder().page(page).content(content).region(region).orderBy(orderBy).build()));
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId, @RequestBody UpdatePostRequest updatePostRequest) {
        return ResponseEntity.ok(postService.updatePost(userPrincipal.getUserId(),postId, updatePostRequest));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Boolean> deletePost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.deletePost(userPrincipal.getUserId(),postId));
    }

    @Operation(summary = "내가 쓴글 조회", description = "글쓴이가 자신인 글을 조회 합니다.")
    @GetMapping("/my-post")
    public ResponseEntity<Page<PostResponse>> getMyPosts(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestParam(required = true) int page, @RequestParam(required = false) String orderBy) {
        return ResponseEntity.ok(postService.getMyPosts(userPrincipal.getUserId(), PostSearchRequest.builder().page(page).orderBy(orderBy).build()));
    }

    @Operation(summary = "게시글 추천")
    @PostMapping("/recommend/{postId}")
    public ResponseEntity<Boolean> recommendPost(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.recommend(userPrincipal.getUserId(), postId));
    }

    @Operation(summary = "게시글 추천 취소")
    @PostMapping("/recommend/cancel/{postId}")
    public ResponseEntity<Boolean> recommendCancel(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        return ResponseEntity.ok(postService.cancelRecommend(userPrincipal.getUserId(), postId));
    }

    @Operation(summary = "게시글 단건 조회")
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId) {
        Long userId = Optional.ofNullable(userPrincipal).map(UserPrincipal::getUserId).orElse(null);
        return ResponseEntity.ok(postService.getOnePostById(userId, postId));
    }

    @Operation(summary = "유저의 게시글 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PostResponse>> getPostByUser(@PathVariable Long userId, @RequestParam(required = true) int page) {
        return ResponseEntity.ok(postService.getMyPosts(userId, PostSearchRequest.builder().build()));
    }
}
