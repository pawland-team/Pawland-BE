package com.pawland.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "댓글 생성 요청")
public class CreateCommentRequest {
    private Long postId;
    private String content;

    public CreateCommentRequest(Long postId, String content) {
        this.postId = postId;
        this.content = content;
    }
}
