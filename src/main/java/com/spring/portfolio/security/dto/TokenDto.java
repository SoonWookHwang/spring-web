package com.spring.portfolio.security.dto;

import lombok.Data;

@Data
public class TokenDto {

  private String accessToken;
  private String refreshToken;

}
