package com.pawland.order.service;

import com.pawland.order.domain.OrderStatus;
import com.pawland.order.dto.response.OrderResponse;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.service.ProductService;
import com.pawland.user.domain.User;
import com.pawland.user.domain.UserType;
import com.pawland.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    private final List<User> list = new ArrayList<>();

    @PostConstruct
    void init() {
        User tester = User.builder().email("test@test.com")
                .password("123123")
                .nickname("010-1111-1111")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(UserType.GOOGLE)
                .build();

        userRepository.save(tester);

        list.add(tester);

        User tester2 = User.builder().email("test2@test.com")
                .password("123123")
                .nickname("010-1111-2222")
                .nickname("tester2")
                .introduce("tester2입니다.")
                .type(UserType.GOOGLE)
                .build();

        userRepository.save(tester2);

        CreateProductRequest createProductRequest = new CreateProductRequest(
                "사료",
                "CAT",
                "NEW",
                "상품1",
                10000,
                "상품입니다.",
                "서울시 강서구",
                null,
                null);

        list.add(tester2);

        productService.createProduct(1L, createProductRequest);
    }

    @DisplayName("주문 생성 성공")
    @Test
    void createOrder() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);

        //when
        OrderResponse orderResponse = orderService.createOrder(buyer.getId(), 1L);

        //then
        Assertions.assertEquals(1L,orderResponse.getId());
        Assertions.assertEquals(seller.getNickname(),orderResponse.getSeller().getNickname());
        Assertions.assertEquals(buyer.getNickname(),orderResponse.getBuyer().getNickname());
        Assertions.assertEquals("상품1",orderResponse.getProduct().getName());
    }

    @DisplayName("주문 단일 조회")
    @Test
    void getOneOrderById() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertEquals(1L,oneOrderById.getId());
        Assertions.assertEquals(seller.getNickname(),oneOrderById.getSeller().getNickname());
        Assertions.assertEquals(buyer.getNickname(),oneOrderById.getBuyer().getNickname());
        Assertions.assertEquals("상품1",oneOrderById.getProduct().getName());
    }

    @DisplayName("판매자 거래 완료")
    @Test
    void doneOrderBySeller() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        orderService.doneOrder(seller.getId(), 1L);
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertTrue(oneOrderById.isSellerCheck());
        Assertions.assertFalse(oneOrderById.isBuyerCheck());
        Assertions.assertEquals(OrderStatus.PROCEEDING,oneOrderById.getOrderStatus());
    }

    @DisplayName("구매자 거래 완료")
    @Test
    void doneOrderByBuyer() {
        //given
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        orderService.doneOrder(buyer.getId(), 1L);
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertTrue(oneOrderById.isBuyerCheck());
        Assertions.assertFalse(oneOrderById.isSellerCheck());
        Assertions.assertEquals(OrderStatus.PROCEEDING,oneOrderById.getOrderStatus());
    }

    @DisplayName("판매자 구매자 모두 거래 완료")
    @Test
    void doneOrderBoth() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        orderService.doneOrder(seller.getId(), 1L);
        orderService.doneOrder(buyer.getId(), 1L);
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertTrue(oneOrderById.isBuyerCheck());
        Assertions.assertTrue(oneOrderById.isSellerCheck());
        Assertions.assertEquals(OrderStatus.DONE,oneOrderById.getOrderStatus());
    }

    @DisplayName("판매자의 주문 취소")
    @Test
    void cancelOrderBySeller() {
        //given
        User seller = list.get(0);
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        orderService.cancelOrder(seller.getId(), 1L);
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertEquals(OrderStatus.CANCEL,oneOrderById.getOrderStatus());
    }

    @DisplayName("구매자의 주문 취소")
    @Test
    void cancelOrderByBuyer() {
        //given
        User buyer = list.get(1);
        orderService.createOrder(buyer.getId(), 1L);

        //when
        orderService.cancelOrder(buyer.getId(), 1L);
        OrderResponse oneOrderById = orderService.getOneOrderById(1L);

        //then
        Assertions.assertEquals(OrderStatus.CANCEL,oneOrderById.getOrderStatus());
    }

}
