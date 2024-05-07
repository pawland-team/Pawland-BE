package com.pawland.post.repository;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.Region;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.user.domain.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                        eqRegion(postSearchRequest.getRegion()))
                .orderBy(createOrderSpecifier(postSearchRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(posts,pageable,posts.size());
    }

    public Page<Post> getMyPosts(Long userId,Pageable pageable,PostSearchRequest postSearchRequest) {
        List<Post> myPosts = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.author, QUser.user)
                .fetchJoin()
                .where(post.author.id.eq(userId))
                .orderBy(createOrderSpecifier(postSearchRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(myPosts,pageable,myPosts.size());
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

    private OrderSpecifier[] createOrderSpecifier(PostSearchRequest postSearchRequest) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (Objects.isNull(postSearchRequest.getOrderBy())) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.createdDate));
        } else if (postSearchRequest.getOrderBy().equals("view")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.views));
        }else if (postSearchRequest.getOrderBy().equals("recommend")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.recommends.size()));
        }else if (postSearchRequest.getOrderBy().equals("comment")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.comments.size()));
        }

    return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
