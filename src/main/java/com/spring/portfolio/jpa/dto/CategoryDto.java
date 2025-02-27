package com.spring.portfolio.jpa.dto;

import com.spring.portfolio.jpa.entity.ProductCategory;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {
  private Long id;
  private String name;

  private Long parentId;
  List<CategoryDto> children = new ArrayList<>();

  public CategoryDto(Long id, String name,Long parentId) {
    this.id = id;
    this.name = name;
    this.parentId = parentId;
  }

  // 하위 계층 자식 카테고리 조회 및 객체 생성
  public void setChildren(List<ProductCategory> children) {
    children.forEach(category -> {
      Long parentCategoryId = (category.getParentCategory()!=null && category.getParentCategory().getParentCategory() != null) ? category.getParentCategory().getParentCategory().getCategoryId() : null;
      CategoryDto child = new CategoryDto(category.getCategoryId(), category.getName(),parentCategoryId);
      this.children.add(child);
    });
  }
}
