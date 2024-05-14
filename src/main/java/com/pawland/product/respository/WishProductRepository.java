package com.pawland.product.respository;

import com.pawland.product.domain.QProduct;
import com.pawland.product.domain.WishProduct;
import com.pawland.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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

    public List<WishProduct> getWishProductByUserId(Long userId) {
        return jpaQueryFactory.selectFrom(wishProduct)
                .leftJoin(wishProduct.product, QProduct.product)
                .fetchJoin()
                .leftJoin(wishProduct.user, QUser.user)
                .fetchJoin()
                .where(wishProduct.user.id.eq(userId))
                .fetch();

    }

}
