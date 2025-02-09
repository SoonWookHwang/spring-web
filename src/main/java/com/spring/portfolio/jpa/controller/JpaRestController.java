package com.spring.portfolio.jpa.controller;

import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.repository.ProductRepository;
import com.spring.portfolio.jpa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/jpa")
public class JpaRestController {

    private final ProductService productService;

    /**
     검색 조건을 통한 데이터 조회
     페이징 처리 및 정렬 적용
     */

    @PostMapping("/getProduct")
    public ResponseEntity<Page<ProductDto>> getData(@RequestBody ProductSearchQueryDto dto) {
        // Product 엔티티를 ProductDto로 변환
        Page<ProductDto> productDtoPage = productService.searchProducts(dto);
        return ResponseEntity.ok(productDtoPage);
    }
}
