package com.pawland.post.dto.request;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.Region;
import com.pawland.user.domain.User;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostWriteRequest {

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String thumbnail;

    private String region;

    @Builder
    public PostWriteRequest(String title, String content, String thumbnail, String region) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.region = region;
    }

    public Post toPostWith(User user) {
        return Post.builder()
            .author(user)
            .title(title)
            .content(content)
            .thumbnail(thumbnail)
            .region(Region.fromString(region))
            .build();
    }
}
