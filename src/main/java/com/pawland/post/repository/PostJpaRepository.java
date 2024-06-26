package com.pawland.post.repository;

import com.pawland.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
}
