package com.spring.portfolio.jpa.service;

import com.spring.portfolio.global.exceptions.ProductException;
import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import com.spring.portfolio.jpa.repository.ProductCategoryRepository;
import com.spring.portfolio.jpa.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductRepository productRepository;
  private final ProductCategoryRepository categoryRepository;

  @Override
  public Page<ProductDto> getAllProducts() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Product> productPage = productRepository.findAll(pageable);
    Page<ProductDto> productDtoPage = productPage.map(ProductDto::fromEntity);
    return productDtoPage;
  }

  //Spring Data JPA의 기본 Query Method 방식
  @Override
  public Page<ProductDto> searchProductsByJPAQueryMethod(ProductSearchQueryDto dto) {
    Sort sort = Sort.by(Sort.Direction.fromString(dto.getOrderType()), dto.getOrderBy());
    Pageable pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);
    Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(
        dto.getProductName(), pageable);
    return productPage.map(ProductDto::fromEntity);
  }

  // @Query annotation JPQL을 사용한  방식
  @Override
  public Page<ProductDto> searchProductsByJPQLQuery(ProductSearchQueryDto dto) {
    Sort sort = Sort.by(Sort.Direction.fromString(dto.getOrderType()), dto.getOrderBy());
    Pageable pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);
    Page<Product> productPage = productRepository.searchByName(dto.getProductName(), pageable);
    return productPage.map(ProductDto::fromEntity);
  }

  //Spring Data JPA의 Specification API를 활용한 동적 쿼리 방식
  @Override
  public Page<ProductDto> searchProductsByJPASpecification(ProductSearchQueryDto dto) {
    Sort sort = Sort.by(Sort.Direction.fromString(dto.getOrderType()), dto.getOrderBy());
    Pageable pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);

    Specification<Product> spec = (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (dto.getProductName() != null && !dto.getProductName().isEmpty()) {
        // 대소문자를 구분하지 않기 위해 both sides to lower case
        predicates.add(
            cb.like(cb.lower(root.get("name")), "%" + dto.getProductName().toLowerCase() + "%"));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    Page<Product> productPage = productRepository.findAll(spec, pageable);
    return productPage.map(ProductDto::fromEntity);
  }

  @Override
  public void insertProduct(ProductDto dto) {
    ProductCategory productCategory = categoryRepository.findById(dto.getCategoryId())
        .orElseThrow(() -> new ProductException("해당되는 카테고리가 없음"));
    Product newProduct = Product.of(dto, productCategory);
    productRepository.save(newProduct);
  }

  @Override
  public void updateProduct(ProductDto dto) {

  }

  @Override
  public void deleteProduct(Long productId) {

  }

  @Override
  public List<ProductCategory> findAllCategories() {
    List<ProductCategory> categories = categoryRepository.findAll();
    return categories;
  }
}
