package com.spring.portfolio.security.service;

import com.spring.portfolio.global.exceptions.CustomSecurityException;
import com.spring.portfolio.security.dto.SignInDto;
import com.spring.portfolio.security.dto.SignUpDto;
import com.spring.portfolio.security.dto.TokenDto;
import com.spring.portfolio.security.entity.PortfolioUser;
import com.spring.portfolio.security.reopository.PortfolioUserRepository;
import com.spring.portfolio.security.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PortfolioUserServiceImpl implements PortfolioUserService{

  private final JwtTokenProvider jwtTokenProvider;
  private final PortfolioUserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;

  @Override
  @Transactional
  public Long signUp(SignUpDto dto){
    try {
      if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
      }
      String encodedPassword = passwordEncoder.encode(dto.getPassword());
      PortfolioUser newUser = PortfolioUser.of(dto.withPassword(encodedPassword));
      return userRepository.save(newUser).getId();
    }catch (Exception e){
      throw new CustomSecurityException("회원가입 실패 caused by : " + e.getMessage());
    }
  }

  @Override
  public TokenDto signIn(SignInDto dto) {
    UserDetails user = userDetailsService.loadUserByUsername(dto.getEmail());
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));
    TokenDto tokenDto = new TokenDto();
    tokenDto.setAccessToken(jwtTokenProvider.generateToken(user.getUsername(), false));
    tokenDto.setRefreshToken(jwtTokenProvider.generateToken(user.getUsername(),true));
    return tokenDto;
  }
}
