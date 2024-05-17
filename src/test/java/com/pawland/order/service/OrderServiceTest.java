package com.pawland.order.service;

import com.pawland.order.domain.OrderStatus;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.service.ProductService;
import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class OrderServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    private List<User> list = new ArrayList<>();

    @BeforeEach
    void init() {
        User tester = User.builder().email("test@test.com")
                .password("123123")
                .nickname("010-1111-1111")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(LoginType.GOOGLE)
                .build();

        userRepository.save(tester);

        list.add(tester);

        User tester2 = User.builder().email("test2@test.com")
                .password("123123")
                .nickname("010-1111-2222")
                .nickname("tester2")
                .introduce("tester2입니다.")
                .type(LoginType.GOOGLE)
                .build();

        userRepository.save(tester2);


        list.add(tester2);

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

    @DisplayName("주문 생성 성공")
    @Test
    @Transactional
    void createOrder() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);

        ProductResponse product = createProduct(seller.getId());

        //when
        OrderResponse orderResponse = orderService.createOrder(buyer.getId(), product.getId());

        //then
        Assertions.assertEquals(seller.getNickname(), orderResponse.getSeller().getNickname());
        Assertions.assertEquals(buyer.getNickname(), orderResponse.getBuyer().getNickname());
        Assertions.assertEquals("상품", orderResponse.getProduct().getName());
    }

    @DisplayName("주문 단일 조회")
    @Test
    @Transactional
    void getOneOrderById() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        ProductResponse product = createProduct(seller.getId());

        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertEquals(seller.getNickname(), oneOrderById.getSeller().getNickname());
        Assertions.assertEquals(buyer.getNickname(), oneOrderById.getBuyer().getNickname());
        Assertions.assertEquals("상품", oneOrderById.getProduct().getName());
    }

    @DisplayName("판매자 거래 완료")
    @Test
    @Transactional
    void doneOrderBySeller() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        ProductResponse product = createProduct(seller.getId());
        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        orderService.doneOrder(seller.getId(), order.getId());
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertTrue(oneOrderById.isSellerCheck());
        Assertions.assertFalse(oneOrderById.isBuyerCheck());
        Assertions.assertEquals(OrderStatus.PROCEEDING, oneOrderById.getOrderStatus());
    }

    @DisplayName("구매자 거래 완료")
    @Test
    @Transactional
    void doneOrderByBuyer() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        ProductResponse product = createProduct(seller.getId());
        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        orderService.doneOrder(buyer.getId(), order.getId());
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertTrue(oneOrderById.isBuyerCheck());
        Assertions.assertFalse(oneOrderById.isSellerCheck());
        Assertions.assertEquals(OrderStatus.PROCEEDING, oneOrderById.getOrderStatus());
    }

    @DisplayName("판매자 구매자 모두 거래 완료")
    @Test
    @Transactional
    void doneOrderBoth() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        ProductResponse product = createProduct(seller.getId());
        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        orderService.doneOrder(seller.getId(), order.getId());
        orderService.doneOrder(buyer.getId(), order.getId());
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertTrue(oneOrderById.isBuyerCheck());
        Assertions.assertTrue(oneOrderById.isSellerCheck());
        Assertions.assertEquals(OrderStatus.DONE, oneOrderById.getOrderStatus());
    }

    @DisplayName("판매자의 주문 취소")
    @Test
    @Transactional
    void cancelOrderBySeller() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        ProductResponse product = createProduct(seller.getId());
        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        orderService.cancelOrder(seller.getId(), order.getId());
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertEquals(OrderStatus.CANCEL, oneOrderById.getOrderStatus());
    }

    @DisplayName("구매자의 주문 취소")
    @Test
    @Transactional
    void cancelOrderByBuyer() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);

        ProductResponse product = createProduct(seller.getId());
        OrderResponse order = orderService.createOrder(buyer.getId(), product.getId());

        //when
        orderService.cancelOrder(buyer.getId(), order.getId());
        OrderResponse oneOrderById = orderService.getOneOrderById(order.getId());

        //then
        Assertions.assertEquals(OrderStatus.CANCEL, oneOrderById.getOrderStatus());
    }

}
