package com.pawland.comment.respository;

import com.pawland.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
}
