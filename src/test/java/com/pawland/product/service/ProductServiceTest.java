package com.pawland.product.service;

import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.exception.ProductException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@Transactional
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    void init() {
        User tester = User.builder()
            .email("test@test.com")
                .password("123123")
                .nickname("tester")
                .introduce("tester입니다.")
                .type(UserType.GOOGLE)
                .build();

        userRepository.save(tester);
    }

    private void createProduct(Long userId,int size) {

        for (int i = 0; i < size; i++) {
            CreateProductRequest createProductRequest = new CreateProductRequest(
                    "사료",
                    "CAT",
                    "NEW",
                    "상품"+i,
                    10000,
                    "상품입니다.",
                    "서울시 강서구",
                    null,
                    null);

            productService.createProduct(userId, createProductRequest);
        }


    }

    @DisplayName("상품 등록")
    @Test
    void createProductTest() {
        //given
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

        //when
        ProductResponse product = productService.createProduct(1L, createProductRequest);

        //then
        Assertions.assertEquals(1L, product.getId());
        Assertions.assertEquals("tester",product.getSeller().getNickname());
    }


    @DisplayName("상품 단일 조회")
    @Test
    void getOneProductById() {
        //given
        createProduct(1L,1);

        //when
        ProductResponse oneProductById = productService.getOneProductById(1L);

        //then
        Assertions.assertEquals(1L, oneProductById.getId());
        Assertions.assertEquals("상품0",oneProductById.getName());
        Assertions.assertEquals("tester",oneProductById.getSeller().getNickname());
    }

    @DisplayName("상품 수정")
    @Test
    void updateProduct() {
        //given
        createProduct(1L,1);

        //when
        ProductResponse updatedProduct = productService.updateProduct(1L, 1L, UpdateProductRequest.builder().name("상품1수정").build());

        //then
        Assertions.assertEquals("상품1수정",updatedProduct.getName());
    }

    @DisplayName("상품 삭제")
    @Test
    @Transactional
    void deleteProduct() {
        //given
        createProduct(1L,1);

        //when
        productService.deleteProduct(1L, 1L);

        //then
        Assertions.assertThrows(ProductException.class, () -> productService.getOneProductById(1L));

    }

    @DisplayName("상품 최신순 8개 조회")
    @Test
    @Transactional
    void getProductsWithPaging() {
        //given
        createProduct(1L,10);

        //when
        List<ProductResponse> products = productService.getProducts(1);

        //then
        Assertions.assertEquals(8, products.size());
        Assertions.assertEquals(10L,products.get(0).getId());
    }
}
