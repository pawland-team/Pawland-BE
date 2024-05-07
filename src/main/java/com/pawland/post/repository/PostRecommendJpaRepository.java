package com.pawland.post.repository;

import com.pawland.post.domain.PostRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRecommendJpaRepository extends JpaRepository<PostRecommend, Long> {
}
