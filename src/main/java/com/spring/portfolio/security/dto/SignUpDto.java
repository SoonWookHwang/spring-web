package com.spring.portfolio.security.dto;

import com.spring.portfolio.security.entity.PortfolioUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {
  private String email;
  private String password;
  private String name;
  private String keyword;

  public SignUpDto withPassword(String password) {
    return new SignUpDto(this.email, password,this.name,this.keyword);
  }
}
