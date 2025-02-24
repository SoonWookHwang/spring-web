package com.spring.portfolio.jpa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
public class SearchResponseDto {
  private Page<ProductDto> productDtoPage;
  private Integer searchDuration;
}