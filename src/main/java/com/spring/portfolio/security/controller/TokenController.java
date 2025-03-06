package com.spring.portfolio.security.controller;

import com.spring.portfolio.security.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

  private final JwtTokenProvider tokenProvider;

  /**
   * 리프레시 토큰을 이용해 새 액세스 토큰을 발급하는 API
   */
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    // 1. 요청 헤더에서 Refresh Token 가져오기
    log.info("refresh token으로 access token 재발급 요청 api 호출됌");
    String refreshToken = request.getHeader("Authorization");
    if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("Refresh Token이 필요합니다.");
    }
    refreshToken = refreshToken.substring(7);
    try {
      String email = tokenProvider.getEmailFromToken(refreshToken);
      String storedToken = tokenProvider.getRefreshToken(email);
      if (!refreshToken.equals(storedToken)) {
        return ResponseEntity.status(401).body("유효하지 않은 Refresh Token입니다.");
      }
      String newAccessToken = tokenProvider.generateToken(email, false);
      response.setHeader("Authorization", "Bearer " + newAccessToken);
      return ResponseEntity.ok().body(newAccessToken);
    } catch (ExpiredJwtException e) {
      return ResponseEntity.status(401).body("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
    } catch (Exception e) {
      log.info("토큰 갱신 중 오류가 발생했습니다.");
      return ResponseEntity.status(500).body("토큰 갱신 중 오류가 발생했습니다.");
    }
  }


}
