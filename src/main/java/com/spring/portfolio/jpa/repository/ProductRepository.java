package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {
}
