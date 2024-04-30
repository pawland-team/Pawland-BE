package com.pawland.post.domain;

import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.global.domain.DefaultImage;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.pawland.post.domain.Region.SEOUL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private String thumbnail = DefaultImage.POST.getImageUrl();

    private Region region = SEOUL;

    private Long views;

    @Builder
    public Post(User author, String title, String content, String thumbnail, Region region) {
        this.author = author;
        this.title = title;
        this.content = content;
        this.thumbnail = isBlank(thumbnail) ? this.thumbnail : thumbnail;
        this.region = isBlank(region) ? this.region : region;
    }

    private boolean isBlank(Region region) {
        return region == null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
