package com.spring.portfolio.security.controller;

import com.spring.portfolio.security.security.JwtRequired;
import com.spring.portfolio.security.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security/token")
@RequiredArgsConstructor
public class TokenController {

  private final JwtTokenProvider tokenProvider;

  /**
   * 리프레시 토큰을 이용해 새 액세스 토큰을 발급하는 API
   */
  @PostMapping("/refresh")
  @JwtRequired
  public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    // 1. 요청 헤더에서 Refresh Token 가져오기
    String refreshToken = request.getHeader("Authorization");

    if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("Refresh Token이 필요합니다.");
    }

    // "Bearer " 제거
    refreshToken = refreshToken.substring(7);

    try {
      // 2. 토큰에서 이메일 추출
      String email = tokenProvider.getEmailFromToken(refreshToken);

      // 3. 저장된 Refresh Token과 비교하여 검증
      String storedToken = tokenProvider.getRefreshToken(email);
      if (!refreshToken.equals(storedToken)) {
        return ResponseEntity.status(403).body("유효하지 않은 Refresh Token입니다.");
      }

      // 4. 새로운 Access Token 발급
      String newAccessToken = tokenProvider.generateToken(email, false);

      // 5. 응답 헤더에 새 Access Token 추가
      response.setHeader("Authorization", "Bearer " + newAccessToken);
      return ResponseEntity.ok().body("새로운 Access Token이 발급되었습니다.");

    } catch (ExpiredJwtException e) {
      return ResponseEntity.status(401).body("Refresh Token이 만료되었습니다. 다시 로그인하세요.");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("토큰 갱신 중 오류가 발생했습니다.");
    }
  }


}
