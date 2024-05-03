package com.pawland.comment.domain;

import com.pawland.comment.dto.request.UpdateCommentRequest;
import com.pawland.global.domain.BaseTimeEntity;
import com.pawland.post.domain.Post;
import com.pawland.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User author;

    private String content;

    @ManyToOne
    private Comment parent;

    @OneToMany(mappedBy = "parent",orphanRemoval = true)
    private List<Comment> reply;

    @Builder
    public Comment(Post post, User author,String content) {
        this.post = post;
        this.author = author;
        this.content = content;
        this.reply = new ArrayList<>();
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public void addReply(Comment comment) {
        this.reply.add(comment);
        comment.parent = this;
    }

    public void update(UpdateCommentRequest updateCommentRequest) {
        this.content = updateCommentRequest.getContent();
    }
}
