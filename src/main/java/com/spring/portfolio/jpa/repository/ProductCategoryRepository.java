package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
