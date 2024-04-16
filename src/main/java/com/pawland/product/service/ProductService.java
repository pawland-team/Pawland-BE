package com.pawland.product.service;

import com.pawland.product.domain.Category;
import com.pawland.product.domain.Product;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.respository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductJpaRepository productJpaRepository;

    public ProductResponse createProduct(String username, CreateProductRequest createProductRequest) {
        Product product = Product.builder()
                .category(Category.getInstance(createProductRequest.getCategory()))
                .name(createProductRequest.getName())
                .price(createProductRequest.getPrice())
                .content(createProductRequest.getContent())
                .region(createProductRequest.getRegion())
                .view(0)
                .build();

        productJpaRepository.save(product);

        return ProductResponse.of(product);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productJpaRepository.findById(productId).orElseThrow(IllegalArgumentException::new);

        return ProductResponse.of(product);
    }

    @Transactional
    public ProductResponse updateProduct(String username, Long productId, UpdateProductRequest updateProductRequest) {
        Product product = productJpaRepository.findById(productId).orElseThrow(IllegalArgumentException::new);

        if (canUpdateOrDelete(Long.valueOf(username), product)) {
            product.update(updateProductRequest);
            return ProductResponse.of(product);
        } else {
            throw new IllegalStateException();
        }


    }

    // todo
    // username UserDetails 구성에 따라 변경
    public boolean deleteProduct(String username, Long productId) {
        Product product = productJpaRepository.findById(productId).orElseThrow(IllegalArgumentException::new);

        if (canUpdateOrDelete(Long.valueOf(username), product)) {
            productJpaRepository.delete(product);
            return true;
        } else {
            throw new IllegalStateException();
        }
    }

    private boolean canUpdateOrDelete(Long userId, Product product) {
        return product.getSeller().getId().equals(userId);
    }
}
