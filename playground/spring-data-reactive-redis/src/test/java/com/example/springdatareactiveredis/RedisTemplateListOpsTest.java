package com.example.springdatareactiveredis;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedisTemplateListOpsTest {

  private static final String LIST_NAME = "demo_list";

  @Autowired
  private ReactiveStringRedisTemplate redisTemplate;
  private ReactiveListOperations<String, String> reactiveListOps;

  @Container
  static final RedisContainer container = new RedisContainer(
      RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    container.start();
    registry.add("spring.redis.url", container::getRedisURI);
  }

  @BeforeEach
  void setUp() {
    reactiveListOps = redisTemplate.opsForList();
  }

  @Test
  void test() {
    Mono<Long> lPush = reactiveListOps.leftPushAll(LIST_NAME, "first", "second")
        .log("Pushed");

    StepVerifier.create(lPush)
        .expectNext(2L)
        .verifyComplete();

    Mono<String> lPop = reactiveListOps.leftPop(LIST_NAME)
        .log("Popped");

    StepVerifier.create(lPop)
        .expectNext("second")
        .verifyComplete();
  }
}
