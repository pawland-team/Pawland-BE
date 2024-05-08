package com.pawland.product.service;

import com.pawland.product.domain.Product;
import com.pawland.product.domain.WishProduct;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.SearchProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.exception.ProductException;
import com.pawland.product.respository.ProductJpaRepository;
import com.pawland.product.respository.ProductRepository;
import com.pawland.product.respository.WishProductJpaRepository;
import com.pawland.product.respository.WishProductRepository;
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
    private final WishProductJpaRepository wishProductJpaRepository;
    private final WishProductRepository wishProductRepository;

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
                .thumbnailImageUrl(createProductRequest.getThumbnailImage())
                .imageUrls(createProductRequest.getImages())
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

    @Transactional
    public boolean deleteProduct(Long userId, Long productId) {
        Product product = getProductById(productId);

        if (canUpdateOrDelete(userId, product)) {
            productJpaRepository.delete(product);
            return true;
        } else {
            throw new ProductException.AccessDeniedException();
        }
    }

    @Transactional
    public Page<ProductResponse> getProducts(SearchProductRequest searchProductRequest) {
        Pageable pageable = PageRequest.of(searchProductRequest.getPage() - 1, searchProductRequest.getSize());
        Page<Product> allProducts = productRepository.getAllProducts(searchProductRequest,pageable);

        return allProducts.map(ProductResponse::of);
    }

    @Transactional
    public boolean wishProduct(Long userId, Long productId) {
        Product productById = getProductById(productId);
        User userById = getUserById(userId);

        WishProduct wishProduct = new WishProduct(productById, userById);

        wishProductJpaRepository.save(wishProduct);

        productById.addWishProduct(wishProduct);
        userById.addWishProduct(wishProduct);

        return true;
    }

    @Transactional
    public boolean cancelWishProduct(Long userId, Long productId) {
        WishProduct wishProductByUserIdAndProductId = wishProductRepository.findWishProductByUserIdAndProductId(userId, productId);

        wishProductByUserIdAndProductId.getProduct().deleteWishProduct(wishProductByUserIdAndProductId);
        wishProductByUserIdAndProductId.getUser().deleteWishProduct(wishProductByUserIdAndProductId);

        wishProductJpaRepository.delete(wishProductByUserIdAndProductId);

        return true;
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
