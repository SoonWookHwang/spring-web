package com.spring.portfolio.security.security;

import com.spring.portfolio.global.exceptions.CustomSecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain)
      throws ServletException, IOException {

    String token = extractToken(request);
    if (!token.isEmpty()) {
      // jwt 인증이 필요한 메서드 확인 - @JwtRequired
      String request_url = request.getRequestURI();
      log.info("request_url : {} ", request_url);

      if (request_url.equals("/security/login") || request_url.startsWith("/public")
          || request_url.equals("/security/auth") || request_url.equals("/security/signin")) {
        chain.doFilter(request, response);
        return;
      }
      try {
        log.info("token : {}", token.substring(0, 7));
        log.info("요청 url : {}", request_url);
        if (jwtTokenProvider.validateToken(token)) {
          setAuthentication(token);
          log.info("인증처리 완료");
        } else {
          log.error("유효하지 않은 토큰: {}", token);
        }
      } catch (ExpiredJwtException e) {
        log.info("access-token 만료");
      } catch (Exception e) {
        log.error("토큰 처리 중 오류 발생: {}", e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }

  private void setAuthentication(String token) {
    String email = jwtTokenProvider.getEmailFromToken(token);
    String role = jwtTokenProvider.getRoleFromToken(token); // JWT에서 role 가져오기
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority(role));
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("SecurityContext에 저장된 인증 객체: {}",
        SecurityContextHolder.getContext().getAuthentication());
    log.info("저장된 사용자 권한: {}",
        SecurityContextHolder.getContext().getAuthentication().getAuthorities());
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : "";
  }
}
