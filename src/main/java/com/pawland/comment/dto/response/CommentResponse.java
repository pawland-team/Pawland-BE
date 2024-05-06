package com.pawland.comment.dto.response;

import com.pawland.comment.domain.Comment;
import com.pawland.user.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "댓글 응답")
public class CommentResponse {
    private Long id;
    private UserResponse author;
    private String content;
    private List<CommentResponse> replies;
    private Long recommendCount;

    private CommentResponse(Long id, UserResponse author, String content, List<CommentResponse> replies,Long recommendCount) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.replies = replies;
        this.recommendCount = recommendCount;
    }

    public static CommentResponse of(Comment comment) {
        return new CommentResponse(comment.getId(),
                UserResponse.of(comment.getAuthor()),
                comment.getContent(),
                comment.getReply().stream().map(CommentResponse::of).toList(),
                (long) comment.getRecommendComments().size());
    }
}
