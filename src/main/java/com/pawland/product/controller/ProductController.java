package com.pawland.product.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.global.config.swagger.SecurityNotRequired;
import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.SearchMyProductRequest;
import com.pawland.product.dto.request.SearchProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Tag(name = "ProductController", description = "상품 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt-cookie")
public class ProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "상품 등록")
    @ApiResponse(responseCode = "200", description = "상품 등록 성공")
    @ApiResponse(responseCode = "500", description = "상품 등록 실패")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid CreateProductRequest createProductRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(userPrincipal.getUserId(), createProductRequest));
    }

    @SecurityNotRequired
    @Operation(summary = "상품 단일 조회")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @ApiResponse(responseCode = "500", description = "상품 조회 실패")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.getOneProductById(userPrincipal.getUserId(), productId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "상품 수정")
    @ApiResponse(responseCode = "200", description = "상품 수정 성공")
    @ApiResponse(responseCode = "500", description = "상품 수정 실패")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId, @RequestBody UpdateProductRequest updateProductRequest) {
        return ResponseEntity.ok(productService.updateProduct(userPrincipal.getUserId(), productId, updateProductRequest));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "상품 삭제")
    @ApiResponse(responseCode = "200", description = "상품 삭제 성공")
    @ApiResponse(responseCode = "500", description = "상품 삭제 실패")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(userPrincipal.getUserId(), productId));
    }

    @SecurityNotRequired
    @Operation(summary = "상품 페이징 조회 및 검색")
    @ApiResponse(responseCode = "200", description = "상품 페이징 조회 성공")
    @ApiResponse(responseCode = "500", description = "상품 페이징 조회 실패")
    @GetMapping
    public Page<ProductResponse> getProducts(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @RequestParam(required = false) List<String> region,
                                             @RequestParam(required = false) List<String> species,
                                             @RequestParam(required = false) List<String> category,
                                             @RequestParam(required = false) String orderBy,
                                             @RequestParam(required = false) String content,
                                             @RequestParam(required = false,defaultValue = "false") Boolean isFree,
                                             @RequestParam(required = true) int page,
                                             @RequestParam(required = true) int size

    ) {
        return productService.getProducts(userPrincipal.getUserId(), SearchProductRequest.builder().region(region).species(species).category(category).isFree(isFree).orderBy(orderBy).content(content).page(page).size(size).build());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "상품 찜하기")
    @PostMapping("/wish/{productId}")
    public ResponseEntity<Boolean> wishProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.wishProduct(userPrincipal.getUserId(), productId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "상품 찜 취소")
    @PostMapping("/wish/cancel/{productId}")
    public ResponseEntity<Boolean> wishCancelProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.cancelWishProduct(userPrincipal.getUserId(), productId));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "내가 등록한 상품 조회")
    @GetMapping("/my-product")
    public ResponseEntity<List<ProductResponse>> getMyProduct(@AuthenticationPrincipal UserPrincipal userPrincipal,@RequestParam(required = false) String type,@RequestParam(required = true) int page,@RequestParam(required = true) int size) {
        return ResponseEntity.ok(productService.getMyProduct(userPrincipal.getUserId(),new SearchMyProductRequest(type,page,size)));
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "나의 관심 상품 조회")
    @GetMapping("/my-wish-product")
    public List<ProductResponse> getMyWishedProduct(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return productService.getWishedProduct(userPrincipal.getUserId());
    }

    @SecurityNotRequired
    @Operation(summary = "유저가 등록한 상품 조회")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductResponse>> getProductByUser(@PathVariable Long userId, @RequestParam(required = true) int page, @RequestParam(required = true) int size) {
        return ResponseEntity.ok(productService.getMyProduct(userId,new SearchMyProductRequest(null,page,size)));
    }
}
