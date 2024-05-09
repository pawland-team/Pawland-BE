package com.pawland.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "댓글 생성 요청")
@NoArgsConstructor
public class CreateCommentRequest {
    private String content;

    public CreateCommentRequest(String content) {
        this.content = content;
    }
}
