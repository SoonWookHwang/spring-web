package com.spring.portfolio.jpa.service;

import com.spring.portfolio.global.exceptions.ProductException;
import com.spring.portfolio.jpa.dto.CategoryDto;
import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import com.spring.portfolio.jpa.repository.ProductCategoryRepository;
import com.spring.portfolio.jpa.repository.ProductRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
    Integer minPrice = dto.getMinPrice();
    Integer maxPrice = dto.getMaxPrice();
    Page<Product> productPage;
    if (dto.getCategoryId() != null) {
      List<ProductCategory> categories = getAllSubCategories(dto.getCategoryId());
      if (minPrice != null && maxPrice != null && maxPrice >= minPrice) {
        productPage = productRepository.findByNameContainingIgnoreCaseAndCategoryInAndPriceBetween(
            dto.getProductName(), categories, minPrice, maxPrice, pageable);
      }else {
        productPage = productRepository.findByNameContainingIgnoreCaseAndCategoryIn(dto.getProductName(), categories,pageable);
      }
    }else{
      // 카테고리 조건이 없을 때는 가격 조건만
      if (minPrice != null && maxPrice != null && maxPrice >= minPrice) {
        productPage = productRepository.findByNameContainingIgnoreCaseAndPriceBetween(
            dto.getProductName(), minPrice, maxPrice, pageable);
      } else {
        // 가격 조건 없이 이름만으로 조회
        productPage = productRepository.findByNameContainingIgnoreCase(
            dto.getProductName(), pageable);
      }
    }
    return productPage.map(ProductDto::fromEntity);
  }

  // @Query annotation JPQL을 사용한  방식
  @Override
  public Page<ProductDto> searchProductsByJPQLQuery(ProductSearchQueryDto dto) {
    Sort sort = Sort.by(Sort.Direction.fromString(dto.getOrderType()), dto.getOrderBy());
    Pageable pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);
    Page<Product> productPage;

    List<Long> categoryIds = null;
    if (dto.getCategoryId() != null) {
      List<ProductCategory> categories = getAllSubCategories(dto.getCategoryId());
      // 카테고리 리스트에서 categoryId만 추출하여 categoryIds 리스트로 변환
      categoryIds = categories.stream()
          .map(ProductCategory::getCategoryId)
          .collect(Collectors.toList());
    }
    // categoryIds가 null일 경우 빈 리스트로 넘기면 쿼리에서 유효하게 처리 가능
    productPage = productRepository.searchByNameAndCategory(
        dto.getProductName(),
        categoryIds,
        dto.getMinPrice(),
        dto.getMaxPrice(),
        pageable
    );

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
        predicates.add(
            cb.like(cb.lower(root.get("name")), "%" + dto.getProductName().toLowerCase() + "%"));
      }

      // 카테고리 필터링 (카테고리 ID가 있을 경우)
      if (dto.getCategoryId() != null) {
        // categoryId로 카테고리 객체를 가져옴
        ProductCategory parentCategory = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new ProductException("해당 카테고리를 찾을 수 없습니다"));

        // 해당 카테고리와 하위 카테고리들을 모두 가져옴
        List<ProductCategory> allCategories = getAllSubCategories(parentCategory.getCategoryId());
        List<Long> categoryIds = new ArrayList<>();
        categoryIds.add(parentCategory.getCategoryId());
        for (ProductCategory subCategory : allCategories) {
          categoryIds.add(subCategory.getCategoryId());
        }
        // 카테고리 조건 추가 (카테고리 ID가 포함된 제품 검색)
        predicates.add(root.get("category").get("categoryId").in(categoryIds));
      }
      // 가격 범위 필터링
      if (dto.getMinPrice() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), dto.getMinPrice()));
      }
      if (dto.getMaxPrice() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), dto.getMaxPrice()));
      }
      return cb.and(predicates.toArray(new Predicate[0]));
    };
    // 해당 Specification을 활용하여 페이지 조회
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

  @Override
  public CategoryDto getCategory(Long categoryId) {
    ProductCategory category = categoryRepository.findById(categoryId).orElseThrow(()->new ProductException("해당 카테고리를 찾을 수 없습니다"));
    Long parentCategoryId = (category.getParentCategory()!=null && category.getParentCategory().getParentCategory() != null) ? category.getParentCategory().getParentCategory().getCategoryId() : null;
    CategoryDto dto = new CategoryDto(category.getCategoryId(),category.getName(),parentCategoryId);
    dto.setChildren(category.getSubCategories());
    return dto;
  }

  // 주어진 categoryId에 대한 모든 하위 카테고리들을 재귀적으로 조회
  public List<ProductCategory> getAllSubCategories(Long parentCategoryId) {
    // 첫 번째 하위 카테고리들 조회
    ProductCategory targetCategory = categoryRepository.findById(parentCategoryId).orElseThrow(()->new ProductException("해당 카테고리가 존재하지 않습니다"));
    List<ProductCategory> subCategories = targetCategory.getSubCategories();
    // 재귀적으로 각 카테고리의 하위 카테고리들을 추가
    List<ProductCategory> allSubCategories = new ArrayList<>();
    allSubCategories.add(targetCategory);
    for (ProductCategory subCategory : subCategories) {
      allSubCategories.add(subCategory);
      getAllSubCategories(subCategory.getCategoryId());
    }
    return allSubCategories;
  }


}
