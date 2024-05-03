package com.pawland.comment.dto.request;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private Long postId;
    private String content;

    public CreateCommentRequest(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
