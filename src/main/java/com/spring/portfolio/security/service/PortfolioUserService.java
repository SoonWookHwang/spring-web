package com.spring.portfolio.security.service;

import com.spring.portfolio.security.dto.SignInDto;
import com.spring.portfolio.security.dto.SignUpDto;
import com.spring.portfolio.security.dto.TokenDto;

public interface PortfolioUserService {

  Long signUp(SignUpDto dto);

  TokenDto signIn(SignInDto dto);

}
