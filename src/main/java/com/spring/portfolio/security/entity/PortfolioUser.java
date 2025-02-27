package com.spring.portfolio.security.entity;

import com.spring.portfolio.jpa.dto.ProductDto;
import com.spring.portfolio.jpa.entity.Product;
import com.spring.portfolio.jpa.entity.ProductCategory;
import com.spring.portfolio.security.dto.SignUpDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PortfolioUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String email;
  private String username;
  private String password;
  private String keyword;


  public static PortfolioUser of(SignUpDto dto) {
    return new PortfolioUser(null, dto.getEmail(), dto.getName(), dto.getPassword(),
        dto.getKeyword());
  }

}
