package com.pawland.comment.controller;

import com.pawland.comment.dto.request.CreateCommentRequest;
import com.pawland.comment.service.CommentService;
import com.pawland.global.config.security.domain.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/{postId}")
    public void createComment(@AuthenticationPrincipal UserPrincipal userPrincipal,@PathVariable Long postId, @RequestBody CreateCommentRequest createCommentRequest) {
        commentService.createComment(userPrincipal.getUserId(), postId, createCommentRequest);
    }
}
