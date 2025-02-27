package com.spring.portfolio.security.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class JwtAspect {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Around("@annotation(com.spring.portfolio.security.security.JwtRequired)")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    // 현재 요청의 HttpServletRequest와 HttpServletResponse 객체 가져오기
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();
    HttpServletResponse response = attributes.getResponse();
    // JWT 검증 로직 실행
    FilterChain chain = (req, res) -> {
      try {
        // 원래의 메서드 실행
        joinPoint.proceed();
      } catch (Throwable throwable) {
        throw new ServletException(throwable);
      }
    };
    jwtAuthenticationFilter.doFilterInternal(request, response, chain);
    // 원래의 메서드 실행
    return joinPoint.proceed();
  }
}