package com.spring.portfolio.jpa.service;

import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.entity.Product;
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
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductDto> getAllProducts(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = productRepository.findAll(pageable);
        Page<ProductDto> productDtoPage = productPage.map(this::convertToDto);
        return productDtoPage;
    }
    public Page<ProductDto> searchProducts(ProductSearchQueryDto dto) {
        Sort sort = Sort.by(Sort.Direction.fromString(dto.getOrderType()), dto.getOrderBy());
        Pageable pageable = PageRequest.of(dto.getPageNum(), dto.getPageSize(), sort);

        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dto.getProductName() != null && !dto.getProductName().isEmpty()) {
                // 대소문자를 구분하지 않기 위해 both sides to lower case
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + dto.getProductName().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        Page<ProductDto> productDtoPage = productPage.map(this::convertToDto);
        return productDtoPage;
    }


    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}
