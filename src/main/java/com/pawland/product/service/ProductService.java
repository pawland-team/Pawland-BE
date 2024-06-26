package com.pawland.product.service;

import com.pawland.product.domain.Product;
import com.pawland.product.domain.WishProduct;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.SearchMyProductRequest;
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

    @Transactional
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

        return ProductResponse.of(product, user);
    }

    @Transactional
    public ProductResponse getOneProductById(Long userId, Long productId) {
        Product product = getProductById(productId);
        product.upView();
        return ProductResponse.of(product, getUserByIdOrGuest(userId));
    }

    @Transactional
    public ProductResponse updateProduct(Long userId, Long productId, UpdateProductRequest updateProductRequest) {
        Product product = getProductById(productId);

        if (canUpdateOrDelete(userId, product)) {
            product.update(updateProductRequest);
            return ProductResponse.of(product, getUserById(userId));
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
    public Page<ProductResponse> getProducts(Long userId, SearchProductRequest searchProductRequest) {
        Pageable pageable = PageRequest.of(searchProductRequest.getPage() - 1, searchProductRequest.getSize());
        Page<Product> allProducts = productRepository.getAllProducts(searchProductRequest, pageable);

        return allProducts.map(product -> ProductResponse.of(product, getUserByIdOrGuest(userId)));
    }

    @Transactional
    public boolean wishProduct(Long userId, Long productId) {

        if (wishProductRepository.findWishProductByUserIdAndProductId(userId, productId) != null) {
            throw new IllegalStateException("이미 찜한 상품입니다.");
        }

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

    public Page<ProductResponse> getWishedProduct(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        return wishProductRepository.getWishProductByUserId(userId,pageable).map(WishProduct::getProduct).map(p -> ProductResponse.of(p, getUserById(userId)));
    }

    public Page<ProductResponse> getMyProduct(Long userId, SearchMyProductRequest searchMyProductRequest) {
        Pageable pageable = PageRequest.of(searchMyProductRequest.getPage() - 1, searchMyProductRequest.getSize());
        return productRepository.getMyProduct(userId, searchMyProductRequest.getType(), pageable).map(product -> ProductResponse.of(product, getUserByIdOrGuest(userId)));
    }

    private Product getProductById(Long productId) {
        return productJpaRepository.findById(productId).orElseThrow(ProductException.NotFoundProduct::new);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserException.NotFoundUser::new);
    }

    private User getUserByIdOrGuest(Long userId) {
        return userRepository.findById(userId)
            .orElse(User.builder()
                .email("guest")
                .password("guest")
                .build()
            );
    }

    private boolean canUpdateOrDelete(Long userId, Product product) {
        return product.getSeller().getId().equals(userId);
    }
}
