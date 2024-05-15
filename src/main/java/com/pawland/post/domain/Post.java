package com.pawland.post.domain;

import com.pawland.comment.domain.Comment;
import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.post.dto.request.UpdatePostRequest;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pawland.global.domain.DefaultImage.DEFAULT_POST_IMAGE;
import static com.pawland.post.domain.Region.SEOUL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotNull
    @Lob
    private String content = "";

    private String thumbnail = DEFAULT_POST_IMAGE.value();

    @Enumerated(EnumType.STRING)
    private Region region = SEOUL;

    private Long views = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    Set<PostRecommend> recommends = new HashSet<>();

    @Builder
    public Post(User author, String title, String content, String thumbnail, Region region) {
        this.author = author;
        this.title = title;
        this.content = isBlank(content) ? this.content : content;
        this.thumbnail = isBlank(thumbnail) ? this.thumbnail : thumbnail;
        this.region = isBlank(region) ? this.region : region;
    }

    public void  updatePost(UpdatePostRequest updatePostRequest) {
        this.title = updatePostRequest.getTitle();
        this.content = updatePostRequest.getContent();
        this.thumbnail = updatePostRequest.getThumbnail();
        this.region = Region.fromString(updatePostRequest.getRegion());
    }

    private boolean isBlank(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).isBlank();
        }
        return false;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void addRecommend(PostRecommend postRecommend) {
        this.recommends.add(postRecommend);
    }

    public void deleteRecommend(PostRecommend postRecommend) {
        this.recommends.remove(postRecommend);
    }

    public void upView() {
        this.views++;
    }
}
