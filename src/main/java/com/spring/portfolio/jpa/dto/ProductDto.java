package com.spring.portfolio.jpa.dto;

import com.spring.portfolio.jpa.entity.Product;
import lombok.Data;

@Data
public class ProductDto {
    private Long productId;
    private String name;
    private Integer price;
    private Integer stock;
    private String categoryName;
}
