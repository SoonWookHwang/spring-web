package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {


}
