package com.example.springredisclusterexample;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisClusterTest {

  @Autowired
  StringRedisTemplate stringRedisTemplate;

  @Test
  void test() {
    var valueOps = stringRedisTemplate.opsForValue();
    valueOps.set("1", "test");

    assertThat(valueOps.get("1")).isEqualTo("test");
  }
}
