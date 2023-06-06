package com.example.redissorting;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BiddingAdapter {

  private static final String PREFIX = "HOTEL-BIDDING::";
  private final RedisTemplate<String, Long> biddingRedisTemplate;

  public Boolean createBidding(Long hotelId, Long userId, Double amount) {
    String key = this.serializeKey(hotelId);
    return biddingRedisTemplate.opsForZSet().add(key, userId, amount);
  }

  public List<Long> getTopBidders(Long hotelId, Integer fetchCount) {
    String key = this.serializeKey(hotelId);
    return biddingRedisTemplate
        .opsForZSet()
        .reverseRangeByScore(key, 0D, Double.MAX_VALUE, 0, fetchCount)
        .stream()
        .collect(Collectors.toList());
  }

  public Double getBidAmount(Long hotelId, Long userId) {
    String key = this.serializeKey(hotelId);
    return biddingRedisTemplate.opsForZSet().score(key, userId);
  }

  public void clear(Long hotelId) {
    String key = this.serializeKey(hotelId);
    biddingRedisTemplate.delete(key);
  }

  private String serializeKey(Long hotelId) {
    return PREFIX + hotelId;
  }
}
