package com.spring.portfolio.security.security;

import com.spring.portfolio.security.entity.PortfolioUser;
import com.spring.portfolio.security.reopository.PortfolioUserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final PortfolioUserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    PortfolioUser user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    return User.builder()
        .username(user.getEmail())
        .password(user.getPassword())
        .authorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())))
        .build();
  }
}