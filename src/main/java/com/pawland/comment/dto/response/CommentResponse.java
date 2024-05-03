package com.pawland.comment.dto.response;

import com.pawland.comment.domain.Comment;
import com.pawland.user.dto.response.UserResponse;
import lombok.Data;

import java.util.List;

@Data
public class CommentResponse {
    private Long id;
    private UserResponse author;
    private String content;
    private List<CommentResponse> replies;

    private CommentResponse(Long id, UserResponse author, String content, List<CommentResponse> replies) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.replies = replies;
    }

    public static CommentResponse of(Comment comment) {
        return new CommentResponse(comment.getId(),
                UserResponse.of(comment.getAuthor()),
                comment.getContent(),
                comment.getReply().stream().map(CommentResponse::of).toList());
    }
}
