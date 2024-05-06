package com.pawland.post.repository;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.Region;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.user.domain.QUser;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.pawland.post.domain.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Post> getPostsBySearch(PostSearchRequest postSearchRequest, Pageable pageable) {

        List<Post> posts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.author, QUser.user)
                .fetchJoin()
                .where(eqTitle(postSearchRequest.getContent()),
                        eqContent(postSearchRequest.getContent()),
                        eqRegion(postSearchRequest.getRegion())
                )
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(posts,pageable,posts.size());
    }

    private BooleanExpression eqContent(String content) {
        return !StringUtils.hasText(content) ? null : post.content.contains(content);
    }

    private BooleanExpression eqTitle(String content) {
        return !StringUtils.hasText(content) ? null : post.title.contains(content);
    }

    private BooleanExpression eqRegion(String region) {
        return !StringUtils.hasText(region) ? null : post.region.eq(Region.fromString(region));
    }
}
