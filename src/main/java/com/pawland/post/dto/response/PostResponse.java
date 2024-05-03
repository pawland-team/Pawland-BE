package com.pawland.post.dto.response;

import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.post.domain.Post;
import com.pawland.user.dto.response.UserResponse;
import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private String region;
    private Long views;
    private UserResponse author;
    private List<CommentResponse> comments;

    private PostResponse(Long id, String title, String content, String thumbnail, String region, Long views, UserResponse author, List<CommentResponse> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.region = region;
        this.views = views;
        this.author = author;
        this.comments = comments;
    }

    public static PostResponse of(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getThumbnail(),
                post.getRegion().name(),
                post.getViews(),
                UserResponse.of(post.getAuthor()),
                post.getComments().stream().map(CommentResponse::of).toList()
        );
    }

}
