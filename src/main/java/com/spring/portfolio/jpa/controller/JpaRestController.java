package com.spring.portfolio.jpa.controller;

import com.spring.portfolio.jpa.dto.CategoryDto;
import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.dto.ProductSearchQueryDto;
import com.spring.portfolio.jpa.dto.SearchResponseDto;
import com.spring.portfolio.jpa.service.ProductService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/jpa")
@Slf4j
public class JpaRestController {

    private final ProductService productService;

    /**
     * 검색 조건을 통한 데이터 조회
     * 페이징 처리 및 정렬 적용
     */
    @PostMapping("/getProduct")
    public ResponseEntity<SearchResponseDto> getData(@RequestBody ProductSearchQueryDto dto) {
        LocalDateTime start = LocalDateTime.now();
        // JPA 조회 방식 선택에 따른 분기
        Page<ProductDto> productDtoPage = switch (dto.getJpaType()) {
            case "method" -> {log.info("JPA method 방식 조회"); yield  productService.searchProductsByJPAQueryMethod(dto);}
            case "spec" -> {log.info("JPA specification 방식 조회"); yield productService.searchProductsByJPASpecification(dto);}
            case "jpql" -> {log.info("JPA jpql 방식 조회"); yield productService.searchProductsByJPQLQuery(dto);}
            default -> {log.info("JPA default 방식 조회"); yield productService.searchProductsByJPAQueryMethod(dto);}
        };
        LocalDateTime end = LocalDateTime.now();
        // 검색 시간 계산 (밀리초 단위)
        Integer searchDuration = (int) java.time.Duration.between(start, end).toMillis();

        // 응답 DTO 생성
        SearchResponseDto responseDto = new SearchResponseDto(productDtoPage, searchDuration);

        // ResponseEntity로 반환
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/products")
    public ResponseEntity<?> insertData(@RequestBody ProductDto dto) {
        productService.insertProduct(dto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/products/categories/{categoryId}")
    public ResponseEntity<CategoryDto> list(@PathVariable Long categoryId) {
        CategoryDto category = productService.getCategory(categoryId);
        return ResponseEntity.ok(category);
    }
}
