package com.pawland.product.service;

import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.exception.ProductException;
import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User tester = User.builder()
                .email("test@test.com")
                .password("123123")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(LoginType.GOOGLE)
                .build();
        return userRepository.save(tester);
    }

    @DisplayName("상품 등록")
    @Test
    void createProductTest() {
        //given
        User user = createUser();
        CreateProductRequest createProductRequest = new CreateProductRequest(
                "사료",
                "CAT",
                "NEW",
                "상품",
                10000,
                "상품입니다.",
                "서울시 강서구",
                null,
                null);

        //when
        ProductResponse product = productService.createProduct(user.getId(), createProductRequest);

        //then
        Assertions.assertEquals("tester",product.getSeller().getNickname());
        Assertions.assertEquals("상품", product.getName());
    }


    @DisplayName("상품 단일 조회")
    @Test
    void getOneProductById() {
        //given
        User user = createUser();
        ProductResponse product = productService.createProduct(user.getId(), new CreateProductRequest(
                "사료",
                "CAT",
                "NEW",
                "상품",
                10000,
                "상품입니다.",
                "서울시 강서구",
                null,
                null));
        //when
        ProductResponse oneProductById = productService.getOneProductById(product.getId());

        //then
        Assertions.assertEquals("상품",oneProductById.getName());
        Assertions.assertEquals("tester",oneProductById.getSeller().getNickname());
    }

    @DisplayName("상품 수정")
    @Test
    void updateProduct() {
        //given
        User user = createUser();
        ProductResponse product = productService.createProduct(user.getId(), new CreateProductRequest(
                "사료",
                "CAT",
                "NEW",
                "상품",
                10000,
                "상품입니다.",
                "서울시 강서구",
                null,
                null));

        //when
        ProductResponse updatedProduct = productService.updateProduct(user.getId(), product.getId(), UpdateProductRequest.builder().name("상품1수정").build());

        //then
        Assertions.assertEquals("상품1수정",updatedProduct.getName());
    }

    @DisplayName("상품 삭제")
    @Test
    @Transactional
    void deleteProduct() {
        //given
        User user = createUser();
        ProductResponse product = productService.createProduct(user.getId(), new CreateProductRequest(
                "사료",
                "CAT",
                "NEW",
                "상품",
                10000,
                "상품입니다.",
                "서울시 강서구",
                null,
                null));

        //when
        productService.deleteProduct(user.getId(), product.getId());

        //then
        Assertions.assertThrows(ProductException.NotFoundProduct.class, () -> productService.getOneProductById(product.getId()));

    }

    @DisplayName("상품 최신순 8개 조회")
    @Test
    @Transactional
    void getProductsWithPaging() {
        //given
        User user = createUser();

        for (int i = 0; i < 10; i++) {
            ProductResponse product = productService.createProduct(user.getId(), new CreateProductRequest(
                    "사료",
                    "CAT",
                    "NEW",
                    "상품",
                    10000,
                    "상품입니다.",
                    "서울시 강서구",
                    null,
                    null));
        }


        //when
        List<ProductResponse> products = productService.getProducts(1);

        //then
        Assertions.assertEquals(8, products.size());
    }
}