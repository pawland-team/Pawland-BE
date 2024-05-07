package com.pawland.post.domain;

import com.pawland.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostRecommend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    public PostRecommend(Post post, User user) {
        this.post = post;
        this.user = user;
        post.addRecommend(this);
        user.addRecommend(this);
    }
}
