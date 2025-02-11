package com.spring.portfolio.jpa.dto;

import lombok.Data;

@Data
public class ProductSearchQueryDto {
    private String productName;
    private Integer pageNum = 0; // 기본값 0
    private Integer pageSize = 10; // 기본값 10
    private String orderBy = "id"; // 정렬 기본값
    private String orderType = "ASC";
    private String jpaType = "method"; //  검색 jpa 방식 선택
}
