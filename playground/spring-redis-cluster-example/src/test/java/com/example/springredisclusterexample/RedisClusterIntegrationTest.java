package com.example.springredisclusterexample;

import com.redis.testcontainers.RedisClusterContainer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(classes = SpringRedisClusterExampleApplication.class)
@ActiveProfiles("test")
public class RedisClusterIntegrationTest {

  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Container
  static final RedisClusterContainer container = new RedisClusterContainer(
      RedisClusterContainer.DEFAULT_IMAGE_NAME.withTag(RedisClusterContainer.DEFAULT_TAG));

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    container.start();
    registry.add("spring.redis.cluster.nodes", () -> container.getRedisURI().replace("redis://", ""));
    registry.add("spring.redis.cluster.enabled", () -> true);
  }

  @Test
  void test() {
    Assertions.assertThat(stringRedisTemplate.getConnectionFactory().getClusterConnection().ping()).isEqualTo("PONG");
  }
}
