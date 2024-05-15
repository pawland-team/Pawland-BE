package com.pawland.product.respository;

import com.pawland.product.domain.QProduct;
import com.pawland.product.domain.WishProduct;
import com.pawland.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pawland.product.domain.QWishProduct.wishProduct;


@Repository
@RequiredArgsConstructor
public class WishProductRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public WishProduct findWishProductByUserIdAndProductId(Long userId, Long productId) {

        return jpaQueryFactory.selectFrom(wishProduct)
                .leftJoin(wishProduct.product, QProduct.product)
                .fetchJoin()
                .leftJoin(wishProduct.user, QUser.user)
                .fetchJoin()
                .where(wishProduct.product.id.eq(productId))
                .where(wishProduct.user.id.eq(userId))
                .fetchOne();
    }

    public Page<WishProduct> getWishProductByUserId(Long userId, Pageable pageable) {
        List<WishProduct> wishProducts = jpaQueryFactory.selectFrom(wishProduct)
                .leftJoin(wishProduct.product, QProduct.product)
                .fetchJoin()
                .leftJoin(wishProduct.user, QUser.user)
                .fetchJoin()
                .where(wishProduct.user.id.eq(userId))
                .orderBy(wishProduct.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(wishProduct.count())
                .from(wishProduct)
                .where(wishProduct.user.id.eq(userId));

        return PageableExecutionUtils.getPage(wishProducts,pageable,countQuery::fetchOne);

    }

}
