package com.pawland.product.respository;

import com.pawland.product.domain.Product;
import com.pawland.product.domain.Status;
import com.pawland.user.domain.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pawland.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Product> getAllProducts(Pageable pageable) {

        List<Product> products = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.seller, QUser.user)
                .fetchJoin()
                .where(product.status.eq(Status.SELLING))
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(products, pageable, products.size());
    }
}
