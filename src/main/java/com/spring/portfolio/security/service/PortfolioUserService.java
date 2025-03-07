package com.spring.portfolio.security.service;

import com.spring.portfolio.security.dto.SignInDto;
import com.spring.portfolio.security.dto.SignUpDto;
import com.spring.portfolio.security.dto.TokenDto;
import com.spring.portfolio.security.entity.PortfolioUser;
import java.util.List;

public interface PortfolioUserService {

  Long signUp(SignUpDto dto);

  TokenDto signIn(SignInDto dto);

  List<PortfolioUser> getUsersToEmail();

}
