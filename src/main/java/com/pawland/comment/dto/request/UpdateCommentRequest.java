package com.pawland.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(name = "댓글 수정 요청")
@NoArgsConstructor
public class UpdateCommentRequest {
    private String content;

    public UpdateCommentRequest(String content) {
        this.content = content;
    }
}
