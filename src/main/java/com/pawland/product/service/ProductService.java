package com.pawland.product.service;

import com.pawland.product.domain.Product;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.SearchProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.product.respository.ProductRepository;
import com.pawland.user.domain.User;
import com.pawland.user.exception.UserException;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final UserRepository userRepository;
    private final ProductJpaRepository productJpaRepository;
    private final ProductRepository productRepository;

    public ProductResponse createProduct(Long userId, CreateProductRequest createProductRequest) {

        User user = getUserById(userId);

        Product product = Product.builder()
                .category(createProductRequest.getCategory())
                .species(createProductRequest.getSpecies())
                .condition(createProductRequest.getCondition())
                .name(createProductRequest.getName())
                .price(createProductRequest.getPrice())
                .content(createProductRequest.getContent())
                .region(createProductRequest.getRegion())
                .seller(user)
                .build();

        productJpaRepository.save(product);

        return ProductResponse.of(product);
    }

    public ProductResponse getOneProductById(Long productId) {
        Product product = getProductById(productId);

        return ProductResponse.of(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long userId, Long productId, UpdateProductRequest updateProductRequest) {
        Product product = getProductById(productId);

        if (canUpdateOrDelete(userId, product)) {
            product.update(updateProductRequest);
            return ProductResponse.of(product);
        } else {
            throw new ProductException.AccessDeniedException();
        }
    }

    public boolean deleteProduct(Long userId, Long productId) {
        Product product = getProductById(productId);

        if (canUpdateOrDelete(userId, product)) {
            productJpaRepository.delete(product);
            return true;
        } else {
            throw new ProductException.AccessDeniedException();
        }
    }

    public Page<ProductResponse> getProducts(SearchProductRequest searchProductRequest) {
        Pageable pageable = PageRequest.of(searchProductRequest.getPage() - 1, searchProductRequest.getSize());
        Page<Product> allProducts = productRepository.getAllProducts(searchProductRequest,pageable);

        return allProducts.map(ProductResponse::of);
    }

    private Product getProductById(Long productId) {
        return productJpaRepository.findById(productId).orElseThrow(ProductException.NotFoundProduct::new);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserException.NotFoundUser::new);
    }

    private boolean canUpdateOrDelete(Long userId, Product product) {
        return product.getSeller().getId().equals(userId);
    }
}