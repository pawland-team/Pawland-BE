package com.pawland.product.respository;

import com.pawland.post.domain.Region;
import com.pawland.product.domain.Category;
import com.pawland.product.domain.Product;
import com.pawland.product.domain.Species;
import com.pawland.product.domain.Status;
import com.pawland.product.dto.request.SearchProductRequest;
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

import static com.pawland.product.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<Product> getAllProducts(SearchProductRequest searchProductRequest,Pageable pageable) {

        List<Product> products = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.seller, QUser.user)
                .fetchJoin()
                .where(product.status.eq(Status.SELLING),
                        eqRegion(searchProductRequest.getRegion()),
                        eqSpecies(searchProductRequest.getSpecies()),
                        eqCategory(searchProductRequest.getCategory()),
                        eqPrice(searchProductRequest.getPrice())
                )
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(products, pageable, products.size());
    }

    private BooleanExpression eqRegion(String region) {
        return !StringUtils.hasText(region) ? null : product.region.eq(Region.fromString(region));
    }

    private BooleanExpression eqSpecies(String species) {
        return !StringUtils.hasText(species) ? null : product.species.eq(Species.getInstance(species));
    }

    private BooleanExpression eqCategory(String category) {
        return !StringUtils.hasText(category) ? null : product.category.eq(Category.getInstance(category));
    }

    private BooleanExpression eqPrice(int price) {
        if (price <= 0) {
            return null;
        } else {
            return product.price.eq(price);
        }
    }
}
