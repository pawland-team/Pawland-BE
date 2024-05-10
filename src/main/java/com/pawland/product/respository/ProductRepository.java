package com.pawland.product.respository;

import com.pawland.post.domain.Region;
import com.pawland.product.domain.Category;
import com.pawland.product.domain.Product;
import com.pawland.product.domain.Species;
import com.pawland.product.domain.Status;
import com.pawland.product.dto.request.SearchProductRequest;
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
                        eqPrice(searchProductRequest.getIsFree()),
                        searchContentOrName(searchProductRequest.getContent())
                )
                .orderBy(createOrderSpecifier(searchProductRequest))
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

    private BooleanExpression eqPrice(String price) {
        if (price == null) {
            return null;
        }
        if (price.equals("free")) {
            return product.price.eq(0);
        }
        return null;
    }

    private BooleanExpression searchContentOrName(String content) {
        return StringUtils.hasText(content) ? product.content.like("%" + content + "%").or(product.name.like("%" + content + "%")) : null;
    }

    private OrderSpecifier[] createOrderSpecifier(SearchProductRequest searchProductRequest) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (Objects.isNull(searchProductRequest.getOrderBy())) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.createdDate));
        } else if (searchProductRequest.getOrderBy().equals("높은가격순")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.price));
        }else if (searchProductRequest.getOrderBy().equals("낮은가격순")){
            orderSpecifiers.add(new OrderSpecifier(Order.ASC, product.price));
        }else if (searchProductRequest.getOrderBy().equals("조회순")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.view));
        }else if (searchProductRequest.getOrderBy().equals("인기순")){
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.wishProducts.size()));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
