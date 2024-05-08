package com.pawland.review;

import com.pawland.order.dto.response.OrderResponse;
import com.pawland.order.service.OrderService;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.service.ProductService;
import com.pawland.review.dto.request.CreateReviewRequest;
import com.pawland.review.dto.response.OrderReviewResponse;
import com.pawland.review.service.ReviewService;
import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReviewServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    ReviewService reviewService;

    @Autowired
    ProductService productService;

    @Autowired
    OrderService orderService;

    @DisplayName("리뷰 작성 테스트")
    @Test
    void createReview() {
        //given
        User user = createUser1();
        User user2 = createUser2();
        ProductResponse product = createProduct(user.getId());
        OrderResponse order = orderService.createOrder(user2.getId(), product.getId());
        orderService.doneOrder(user.getId(), order.getId());
        orderService.doneOrder(user2.getId(), order.getId());

        //when
        OrderReviewResponse review = reviewService.createReview(user2.getId(), order.getId(), new CreateReviewRequest("리뷰입니다.", 4.5));
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertEquals(review,oneOrderById.getOrderReviewResponse());
    }

    private User createUser1() {
        User tester = User.builder().email("test@test.com")
                .password("123123")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(LoginType.GOOGLE)
                .build();

        userRepository.save(tester);

        return tester;
    }

    private User createUser2() {
        User tester = User.builder().email("test2@test.com")
                .password("123123")
                .nickname("tester2")
                .introduce("tester2입니다.")
                .type(LoginType.GOOGLE)
                .build();

        userRepository.save(tester);

        return tester;
    }

    private ProductResponse createProduct(Long userId) {
        CreateProductRequest createProductRequest = new CreateProductRequest(
                "사료",
                "고양이",
                "새상품",
                "상품",
                10000,
                "상품입니다.",
                "서울",
                null,
                null);

        return productService.createProduct(userId, createProductRequest);
    }

}
