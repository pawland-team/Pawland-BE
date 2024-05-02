package com.pawland.comment.dto.request;

import lombok.Data;

@Data
public class CreateCommentRequest {
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
