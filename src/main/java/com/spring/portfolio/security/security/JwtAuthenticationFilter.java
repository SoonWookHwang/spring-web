package com.spring.portfolio.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String token = extractToken(request);
    String request_url = request.getRequestURI();
    log.info("요청 url : {}", request_url);

    HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
    boolean requiresJwt = handlerMethod != null && handlerMethod.hasMethodAnnotation(JwtRequired.class);

    // JWT가 필요하지 않은 경우에는 그냥 체인으로 넘어갑니다.
    if (!requiresJwt) {
      log.info("not jwt required");
      chain.doFilter(request, response);
      return;
    }
    log.info("jwt required");
    // JWT가 필요한 경우에만 토큰 검증
    if (token.isEmpty()) {
      response.sendRedirect("/security/login");
      return;
    }
    try {
      if (jwtTokenProvider.validateToken(token)) {
        setAuthentication(token);
      } else {
        log.error("유효하지 않은 토큰: {}", token);
        response.sendRedirect("/security/login");
        return;
      }
    } catch (ExpiredJwtException e) {
      log.warn("토큰 만료: {}", token);
      handleExpiredAccessToken(request, response);
      return;
    } catch (Exception e) {
      log.error("토큰 처리 중 오류 발생: {}", e.getMessage());
      response.sendRedirect("/security/login");
      return;
    }

    // 토큰이 유효한 경우에만 다음 필터로 이동
    chain.doFilter(request, response);
  }

  private void setAuthentication(String token) {
    String email = jwtTokenProvider.getEmailFromToken(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  private String extractToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : "";
  }

  private void handleExpiredAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String refreshToken = request.getHeader("Refresh-Token");
    log.info("refreshToken : {}", refreshToken);
    if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }
    log.info("11111111111");
    refreshToken = refreshToken.substring(7);
    log.info("22222222222");
    try {
      log.info("33333333333");
      // Refresh Token에서 이메일 추출
      String email = jwtTokenProvider.getEmailFromToken(refreshToken);
      log.info("email : {}", email);

      // 저장된 Refresh Token과 비교하여 검증
      String storedToken = jwtTokenProvider.getRefreshToken(email);
      log.info("storedToken : {}", storedToken);
      if (!refreshToken.equals(storedToken)) {
        log.info("레디스에서 조회가 안됌");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return;
      }
      // 새로운 Access Token 발급
      String newAccessToken = jwtTokenProvider.generateToken(email, false);

      // 새 Access Token을 응답 헤더에 추가
      response.setHeader("Authorization", "Bearer " + newAccessToken);
      response.setStatus(HttpServletResponse.SC_OK);
    } catch (ExpiredJwtException e) {
      response.sendRedirect("/security/login");
    } catch (Exception e) {
      log.info("그외 에러");
      response.sendRedirect("/security/login");
    }
  }
}
