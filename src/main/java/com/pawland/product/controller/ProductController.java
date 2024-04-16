package com.pawland.product.controller;

import com.pawland.product.dto.request.CreateProductRequest;
import com.pawland.product.dto.request.UpdateProductRequest;
import com.pawland.product.dto.response.ProductResponse;
import com.pawland.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/product")
    public ResponseEntity<ProductResponse> createProduct(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateProductRequest createProductRequest) {
        return ResponseEntity.ok(productService.createProduct(userDetails.getUsername(),createProductRequest));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId, @RequestBody UpdateProductRequest updateProductRequest) {
        return ResponseEntity.ok(productService.updateProduct(userDetails.getUsername(),productId, updateProductRequest));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Boolean> deleteProduct(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long productId) {
        return ResponseEntity.ok(productService.deleteProduct(userDetails.getUsername(), productId));
    }

}