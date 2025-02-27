package com.spring.portfolio.security.controller;

import com.spring.portfolio.security.dto.SignInDto;
import com.spring.portfolio.security.dto.SignUpDto;
import com.spring.portfolio.security.dto.TokenDto;
import com.spring.portfolio.security.security.JwtRequired;
import com.spring.portfolio.security.security.JwtTokenProvider;
import com.spring.portfolio.security.service.PortfolioUserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
@Slf4j
public class SecurityRestController {

  private final PortfolioUserService userService;
  private final JwtTokenProvider tokenProvider;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody SignUpDto dto){
    log.info(dto.toString());
    Long newUserId = userService.signUp(dto);
    return ResponseEntity.ok("success 회원가입 성공 새로운 유저의 PK : " + newUserId);
  }

  @PostMapping("/signin")
  public ResponseEntity<?> login(@RequestBody SignInDto dto) {
    TokenDto tokenDto = userService.signIn(dto);
    return ResponseEntity.ok(tokenDto);
  }

  /**
   * 로그아웃: 사용자의 Refresh Token을 삭제
   */
  @GetMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    // 요청 헤더에서 Access Token 가져오기
    String accessToken = request.getHeader("Authorization");

    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("Access Token이 필요합니다.");
    }
    // "Bearer " 제거
    accessToken = accessToken.substring(7);
    try {
      // Access Token에서 이메일 추출
      String email = tokenProvider.getEmailFromToken(accessToken);

      // 저장된 Refresh Token 삭제
      tokenProvider.deleteRefreshToken(email);
      return ResponseEntity.ok().body("로그아웃되었습니다.");
    } catch (ExpiredJwtException e) {
      return ResponseEntity.status(401).body("Access Token이 만료되었습니다.");
    } catch (Exception e) {
      return ResponseEntity.status(500).body("로그아웃 중 오류가 발생했습니다.");
    }
  }

  @GetMapping("/authenticated")
  @JwtRequired
  public ResponseEntity<?> authenticatedApi(){
    return ResponseEntity.ok("인증 통과 됌");
  }
  @GetMapping("/non/authenticated")
  @JwtRequired
  public ResponseEntity<?> nonAuthenticatedApi(){
    return ResponseEntity.ok("인증 유무 상과없이 통과 됌");
  }


}
