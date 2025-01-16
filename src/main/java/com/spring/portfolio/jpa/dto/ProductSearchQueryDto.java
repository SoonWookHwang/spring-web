package com.spring.portfolio.jpa.dto;

import lombok.Data;

@Data
public class ProductSearchQueryDto {
    private String productName;
    private Integer pageNum;
    private Integer pageSize;
    private String orderBy;
    private String orderType;
    private String sort;
}
