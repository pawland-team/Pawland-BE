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
                        eqPrice(searchProductRequest.isFree()),
                        searchContentOrName(searchProductRequest.getContent())
                )
                .orderBy(createOrderSpecifier(searchProductRequest))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.status.eq(Status.SELLING),
                        eqRegion(searchProductRequest.getRegion()),
                        eqSpecies(searchProductRequest.getSpecies()),
                        eqCategory(searchProductRequest.getCategory()),
                        eqPrice(searchProductRequest.isFree()),
                        searchContentOrName(searchProductRequest.getContent())
                );

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    public Page<Product> getMyProduct(Long userId,String type,Pageable pageable) {
        List<Product> products = jpaQueryFactory.selectFrom(product)
                .leftJoin(product.seller, QUser.user)
                .fetchJoin()
                .where(product.seller.id.eq(userId), searchProductType(type))
                .orderBy(product.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(product.count())
                .from(product)
                .where(product.seller.id.eq(userId), searchProductType(type));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    private BooleanExpression eqRegion(List<String> region) {
        if (region == null || region.isEmpty()) {
            return null;
        }
        return product.region.in(region.stream().map(Region::fromString).toList());
    }

    private BooleanExpression eqSpecies(List<String> species) {
        if(species == null || species.isEmpty()) {
            return null;
        }
        return product.species.in(species.stream().map(Species::getInstance).toList());
    }

    private BooleanExpression eqCategory(List<String> category) {
        if (category == null || category.isEmpty()) {
            return null;
        }
        return product.category.in(category.stream().map(Category::getInstance).toList());
    }

    private BooleanExpression eqPrice(Boolean price) {
        if (price == null) {
            return null;
        }
        if (price) {
            return product.price.eq(0);
        }
        return null;
    }

    private BooleanExpression searchProductType(String type) {
        if (type != null) {
            if (type.equals("판매중")) {
                return product.status.eq(Status.SELLING);
            }
            if (type.equals("판매완료")) {
                return product.status.eq(Status.DONE);
            }
        }

        return null;
    }

    private BooleanExpression searchContentOrName(String content) {
        return StringUtils.hasText(content) ? product.content.like("%" + content + "%").or(product.name.like("%" + content + "%")) : null;
    }

    private OrderSpecifier[] createOrderSpecifier(SearchProductRequest searchProductRequest) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        if (Objects.nonNull(searchProductRequest.getOrderBy())) {
            switch (searchProductRequest.getOrderBy()) {
                case "높은 가격순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.price));
                    break;
                case "낮은 가격순":
                    orderSpecifiers.add(new OrderSpecifier(Order.ASC, product.price));
                    break;
                case "조회순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.view));
                    break;
                case "인기순":
                    orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.wishProducts.size()));
                    break;
                default:
                    break;
            }
        }
        
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(new OrderSpecifier(Order.DESC, product.createdDate));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
