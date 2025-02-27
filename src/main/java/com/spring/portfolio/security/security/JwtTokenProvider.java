package com.spring.portfolio.security.security;

import com.spring.portfolio.security.reopository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final RefreshTokenRepository refreshTokenRepository;

  @Value("${jwt.secret}")
  private String secretKey;

  private Key key;

  @Value("${jwt.access-token-validity}")
  private long accessTokenValidity;

  @Value("${jwt.refresh-token-validity}")
  private long refreshTokenValidity;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
  }

  public String generateToken(String email, boolean isRefreshToken) {
    long expirationTime = isRefreshToken ? refreshTokenValidity : accessTokenValidity;
    String token = Jwts.builder()
        .setSubject(email)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    if(isRefreshToken){
      saveRefreshToken(email,token);
    }
    return token;
  }

  public String getEmailFromToken(String token) {
    log.info("getEmailFromToken");
    return Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getSubject();
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException e) {
      log.warn("JWT 토큰이 만료되었습니다: {}", e.getMessage());
      throw e;
    } catch (MalformedJwtException e) {
      log.error("잘못된 형식의 JWT 토큰입니다: {}", e.getMessage());
    } catch (Exception e) {
      log.error("JWT 토큰 검증 실패: {}", e.getMessage());
    }
    return false;
  }

  public void saveRefreshToken(String email, String refreshToken) {
    refreshTokenRepository.saveRefreshToken(email, refreshToken, refreshTokenValidity);
  }

  public String getRefreshToken(String email) {
    return refreshTokenRepository.getRefreshToken(email);
  }

  public void deleteRefreshToken(String email) {
    refreshTokenRepository.deleteRefreshToken(email);
  }
}