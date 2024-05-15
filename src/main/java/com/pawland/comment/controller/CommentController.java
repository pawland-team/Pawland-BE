package com.pawland.comment.controller;

import com.pawland.comment.dto.request.CreateCommentRequest;
import com.pawland.comment.dto.request.UpdateCommentRequest;
import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.comment.service.CommentService;
import com.pawland.global.config.security.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "CommentController", description = "댓글 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt-cookie")
public class CommentController {

    private final CommentService commentService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 등록")
    @PostMapping("/post/{postId}")
    public ResponseEntity<CommentResponse> createComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long postId, @RequestBody CreateCommentRequest createCommentRequest) {
        return ResponseEntity.ok(commentService.createComment(userPrincipal.getUserId(), postId, createCommentRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 수정")
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId, @RequestBody UpdateCommentRequest updateCommentRequest) {
        return ResponseEntity.ok(commentService.updateComment(userPrincipal.getUserId(), commentId, updateCommentRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.deleteComment(userPrincipal.getUserId(), commentId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "댓글 추천")
    @PostMapping("/recommend/{commentId}")
    public ResponseEntity<CommentResponse> recommendComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.recommendComment(userPrincipal.getUserId(), commentId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "대댓글 등록")
    @PostMapping("/reply/{commentId}")
    public ResponseEntity<CommentResponse> createCommentComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId, @RequestBody CreateCommentRequest createCommentRequest) {
        return ResponseEntity.ok(commentService.createCommentComment(userPrincipal.getUserId(), commentId, createCommentRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "대댓글 수정")
    @PutMapping("/reply/{commentId}")
    public ResponseEntity<CommentResponse> updateCommentComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId, @RequestBody UpdateCommentRequest updateCommentRequest) {
        return ResponseEntity.ok(commentService.updateComment(userPrincipal.getUserId(), commentId, updateCommentRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "대댓글 삭제")
    @DeleteMapping("/reply/{commentId}")
    public ResponseEntity<Boolean> deleteCommentComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.removeCommentComment(userPrincipal.getUserId(), commentId));
    }


}
