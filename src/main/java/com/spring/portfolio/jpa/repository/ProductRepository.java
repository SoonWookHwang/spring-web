package com.spring.portfolio.jpa.repository;

import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> , JpaSpecificationExecutor<Product> {

    // 1. 메서드 쿼리 방식 (Derived Query Method)
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCaseAndCategoryIn(String name,
        List<ProductCategory> categories, Pageable pageable);
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCaseAndCategoryInAndPriceBetween(String name,
        List<ProductCategory> categories, Integer minPrice, Integer maxPrice, Pageable pageable);
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCaseAndPriceBetween(String name, Integer minPrice, Integer maxPrice,Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // 2. @Query 어노테이션 방식 (JPQL 사용)
    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
        "AND (:categoryIds IS NULL OR p.category.categoryId IN :categoryIds) " +
        "AND (:minPrice IS NULL OR :maxPrice IS NULL OR p.price BETWEEN :minPrice AND :maxPrice)")
    Page<Product> searchByNameAndCategory(
        @Param("name") String name,
        @Param("categoryIds") List<Long> categoryIds,
        @Param("minPrice") Integer minPrice,
        @Param("maxPrice") Integer maxPrice,
        Pageable pageable);
}
