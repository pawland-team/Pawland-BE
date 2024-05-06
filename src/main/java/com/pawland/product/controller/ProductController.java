package com.pawland.product.controller;

import com.pawland.global.config.security.domain.UserPrincipal;
import com.pawland.product.dto.request.CreateProductRequest;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Tag(name = "ProductController", description = "상품 관련 컨트롤러 입니다.")
@SecurityRequirement(name = "jwt")
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 등록")
    @ApiResponse(responseCode = "200", description = "상품 등록 성공")
    @ApiResponse(responseCode = "500", description = "상품 등록 실패")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody @Valid CreateProductRequest createProductRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(userPrincipal.getUserId(), createProductRequest));
    }

    @Operation(summary = "상품 단일 조회")
    @ApiResponse(responseCode = "200", description = "상품 조회 성공")
    @ApiResponse(responseCode = "500", description = "상품 조회 실패")
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getOneProductById(productId));
    }

    @Operation(summary = "상품 수정")
    @ApiResponse(responseCode = "200", description = "상품 수정 성공")
    @ApiResponse(responseCode = "500", description = "상품 수정 실패")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId, @RequestBody UpdateProductRequest updateProductRequest) {
        return ResponseEntity.ok(productService.updateProduct(userPrincipal.getUserId(), productId, updateProductRequest));
    }

    @Operation(summary = "상품 삭제")
    @ApiResponse(responseCode = "200", description = "상품 삭제 성공")
    @ApiResponse(responseCode = "500", description = "상품 삭제 실패")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(userPrincipal.getUserId(), productId));
    }

    @Operation(summary = "상품 페이징 조회")
    @ApiResponse(responseCode = "200", description = "상품 페이징 조회 성공")
    @ApiResponse(responseCode = "500", description = "상품 페이징 조회 실패")
    @GetMapping
    public Page<ProductResponse> getProducts(@RequestParam(required = false) String region, @RequestParam(required = false) String species, @RequestParam(required = false) String category, @RequestParam(required = false) int price, @RequestParam(required = true) int page, @RequestParam(required = true) int size) {
        return productService.getProducts(SearchProductRequest.builder().region(region).species(species).category(category).price(price).page(page).size(size).build());
    }

}