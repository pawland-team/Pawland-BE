package com.pawland.post.repository;

import com.pawland.post.domain.Post;
import com.pawland.post.domain.Region;
import com.pawland.post.dto.request.PostSearchRequest;
import com.pawland.user.domain.QUser;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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

        List<Post> posts = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.author, QUser.user)
                .fetchJoin()
                .where(searchContentOrTitle(postSearchRequest.getContent()),
                        eqRegion(postSearchRequest.getRegion()))
                .orderBy(createOrderSpecifier(postSearchRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(searchContentOrTitle(postSearchRequest.getContent()),
                        eqRegion(postSearchRequest.getRegion()));

        return PageableExecutionUtils.getPage(posts, pageable, countQuery::fetchOne);
    }

    public Page<Post> getMyPosts(Long userId, Pageable pageable, PostSearchRequest postSearchRequest) {
        List<Post> myPosts = jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.author, QUser.user)
                .fetchJoin()
                .where(post.author.id.eq(userId))
                .orderBy(createOrderSpecifier(postSearchRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.author.id.eq(userId));

        return PageableExecutionUtils.getPage(myPosts, pageable, countQuery::fetchOne);
    }

    private BooleanExpression searchContentOrTitle(String content) {
        return StringUtils.hasText(content) ? post.content.like("%" + content + "%").or(post.title.like("%" + content + "%")) : null;
    }

    private BooleanExpression eqRegion(List<String> region) {
        if (region == null || region.isEmpty()) {
            return null;
        }

        return post.region.in(region.stream().map(Region::fromString).toList());
    }

    private OrderSpecifier[] createOrderSpecifier(PostSearchRequest postSearchRequest) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (Objects.nonNull(postSearchRequest.getOrderBy())) {
            switch (postSearchRequest.getOrderBy()) {
                case "조회순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.views));
                    break;
                case "추천순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.recommends.size()));
                    break;
                case "댓글순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.comments.size()));
                    break;
                default:
                    break;
            }
        }

        orderSpecifiers.add(new OrderSpecifier(Order.DESC, post.createdDate));

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}

