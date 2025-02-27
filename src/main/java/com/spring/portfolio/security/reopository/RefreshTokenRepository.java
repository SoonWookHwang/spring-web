package com.spring.portfolio.security.reopository;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RefreshTokenRepository {

  private final RedisTemplate<String, String> redisTemplate;

  public RefreshTokenRepository(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void saveRefreshToken(String email, String refreshToken, long expiration) {
    redisTemplate.opsForValue().set(email, refreshToken, expiration, TimeUnit.MILLISECONDS);
  }

  public String getRefreshToken(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  public void deleteRefreshToken(String email) {
    redisTemplate.delete(email);
  }
}