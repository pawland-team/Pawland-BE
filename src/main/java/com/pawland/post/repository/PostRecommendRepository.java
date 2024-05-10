package com.pawland.post.repository;

import com.pawland.post.domain.PostRecommend;
import com.pawland.post.domain.QPost;
import com.pawland.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.pawland.post.domain.QPostRecommend.postRecommend;

@Repository
@RequiredArgsConstructor
public class PostRecommendRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public PostRecommend getPostRecommendByUserIdAndPostId(Long userId, Long postId) {
        return jpaQueryFactory.selectFrom(postRecommend)
                .leftJoin(postRecommend.post, QPost.post)
                .fetchJoin()
                .leftJoin(postRecommend.user, QUser.user)
                .fetchJoin()
                .where(postRecommend.post.id.eq(postId))
                .where(postRecommend.user.id.eq(userId))
                .orderBy(postRecommend.createdDate.desc())
                .fetchOne();
    }
}
