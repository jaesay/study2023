package com.example.redisdistributedlock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class LockAdapter {

  private final RedisTemplate<LockKey, Long> lockRedisTemplate;
  private final ValueOperations<LockKey, Long> lockOperation;

  public LockAdapter(RedisTemplate<LockKey, Long> lockRedisTemplate) {
    this.lockRedisTemplate = lockRedisTemplate;
    this.lockOperation = lockRedisTemplate.opsForValue();
  }

  public Boolean holdLock(Long hotelId, Long userId) {
    LockKey lockKey = LockKey.from(hotelId);
    return lockOperation.setIfAbsent(lockKey, userId, Duration.ofSeconds(10));
  }

  public Long checkLock(Long hotelId) {
    LockKey lockKey = LockKey.from(hotelId);
    return lockOperation.get(lockKey);
  }

  public void clearLock(Long hotelId) {
    lockRedisTemplate.delete(LockKey.from(hotelId));
  }
}
