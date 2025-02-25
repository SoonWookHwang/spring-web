package com.spring.portfolio.jpa.controller;

import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.entity.ProductCategory;
import com.spring.portfolio.jpa.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/jpa")
public class JpaPageController {
    private final ProductService productService;


    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("pageTitle","product 검색 페이지");
        Page<ProductDto> data = productService.getAllProducts();
        model.addAttribute("productList", data.getContent() );
        return "product/search-product";
    }
    @GetMapping("/products/update/{targetId}")
    public String updateProductPage(@PathVariable Long targetId, Model model){
        model.addAttribute("pageTitle", "product 수정 페이지");
        List<ProductCategory> categories = productService.findAllCategories(); // 카테고리 리스트 조회
        model.addAttribute("categories", categories);
        ProductDto found = productService.getProduct(targetId);
        model.addAttribute("targetProduct", found);
        return "product/insert-product";
    }
    @GetMapping("/products/add")
    public String productsInsert(Model model) {
        model.addAttribute("pageTitle", "product 삽입 페이지");
        List<ProductCategory> categories = productService.findAllCategories(); // 카테고리 리스트 조회
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", new ProductDto());
        return "product/insert-product";
    }
}
