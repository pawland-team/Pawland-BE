package com.pawland.product.respository;

import com.pawland.product.domain.WishProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishProductJpaRepository extends JpaRepository<WishProduct, Long> {
}
