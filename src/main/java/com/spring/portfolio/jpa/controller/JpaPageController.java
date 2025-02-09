package com.spring.portfolio.jpa.controller;

import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
}
