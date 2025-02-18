package com.spring.portfolio.jpa.dto;

import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import lombok.Data;

@Data
public class ProductDto {

    // 조회용 필드
    private Long productId;
    private String name;
    private Integer price;
    private Integer stock;
    private String categoryName;


    // 생성용 필드
    private Long categoryId;

    public static ProductDto fromEntity(Product product) {
        ProductDto dto = new ProductDto();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setCategoryName(product.getCategory().getName());
        return dto;
    }
}
