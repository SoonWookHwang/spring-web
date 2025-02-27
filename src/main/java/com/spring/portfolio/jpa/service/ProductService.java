package com.spring.portfolio.jpa.service;

import com.spring.portfolio.jpa.dto.CategoryDto;
import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.entity.ProductCategory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

//@Service
public interface ProductService {

  /*
   * 조회 관련 메서드
   * */
  ProductDto getProduct(Long productId);

  Page<ProductDto> getAllProducts();

  //Spring Data JPA의 기본 Query Method 방식
  Page<ProductDto> searchProductsByJPAQueryMethod(ProductSearchQueryDto dto);

  // @Query annotation JPQL을 사용한  방식
  Page<ProductDto> searchProductsByJPQLQuery(ProductSearchQueryDto dto);

  //Spring Data JPA의 Specification API를 활용한 동적 쿼리 방식
  Page<ProductDto> searchProductsByJPASpecification(ProductSearchQueryDto dto);


  /*
   * 삽입 관련 메서드
   * */
  Long insertProduct(ProductDto dto);

  /*
   * 수정 관련 메서드
   * */
  Long updateProduct(ProductDto dto);

  /*
   * 삭제 관련 메서드
   * */
  void deleteProduct(List<Long> productIds);


  /*
   * 카테고리 관련 메서드
   * */
  List<ProductCategory> findAllCategories();

  CategoryDto getCategory(Long categoryId);
}
