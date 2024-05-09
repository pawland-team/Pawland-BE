package com.pawland.post.dto.response;

import com.pawland.comment.dto.response.CommentResponse;
import com.pawland.post.domain.Post;
import com.pawland.post.domain.PostRecommend;
import com.pawland.user.domain.User;
import com.pawland.user.dto.response.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
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
    private LocalDateTime createdAt;
    private int recommendCount;
    private boolean isRecommended;

    private PostResponse(Long id, String title, String content, String thumbnail, String region, Long views, UserResponse author, List<CommentResponse> comments,LocalDateTime createdAt,int recommendCount,boolean isRecommended) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.region = region;
        this.views = views;
        this.author = author;
        this.comments = comments;
        this.createdAt = createdAt;
        this.recommendCount = recommendCount;
        this.isRecommended = isRecommended;
    }

    public static PostResponse of(Post post, User user) {

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getThumbnail(),
                post.getRegion().getName(),
                post.getViews(),
                UserResponse.of(post.getAuthor()),
                post.getComments().stream().map(CommentResponse::of).toList(),
                post.getCreatedDate(),
                post.getRecommends().size(),
                post.getRecommends().stream().map(PostRecommend::getUser).toList().contains(user)
        );
    }

}
