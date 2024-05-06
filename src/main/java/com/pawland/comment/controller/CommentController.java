package com.pawland.comment.controller;

import com.pawland.comment.dto.request.CreateCommentRequest;
import com.pawland.comment.dto.request.UpdateCommentRequest;
import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.comment.service.CommentService;
import com.pawland.global.config.security.domain.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Tag(name = "CommentController", description = "댓글 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody CreateCommentRequest createCommentRequest) {
        return commentService.createComment(userPrincipal.getUserId(), createCommentRequest);
    }

    @PutMapping("/{commentId}")
    public CommentResponse updateComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId, @RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(userPrincipal.getUserId(), commentId, updateCommentRequest);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        commentService.deleteComment(userPrincipal.getUserId(),commentId);
    }

    @PostMapping("/{commentId}")
    public void recommendComment(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long commentId) {
        commentService.recommendComment(userPrincipal.getUserId(),commentId);
    }
}
