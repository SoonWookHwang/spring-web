package com.spring.portfolio.jpa.entity;

import com.spring.portfolio.jpa.dto.ProductDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product extends TimeStamps{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private Integer price;
    private Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ProductCategory category;


    public static Product of(ProductDto dto, ProductCategory category) {
        return new Product(null, dto.getName(), dto.getPrice(), dto.getStock(), category);
    }

    public void update(ProductDto dto,ProductCategory updateCategory){
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stock = dto.getStock();
        this.category = updateCategory;
    }

//    @Transactional
    public void decreaseStockToBatch(){
        this.stock = Math.max((this.stock - 1), 0);
    }
}