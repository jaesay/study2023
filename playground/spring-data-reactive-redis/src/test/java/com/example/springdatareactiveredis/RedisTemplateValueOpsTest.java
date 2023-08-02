package com.example.springdatareactiveredis;

import com.redis.testcontainers.RedisContainer;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedisTemplateValueOpsTest {

  @Autowired
  private ReactiveRedisTemplate<String, Employee> redisTemplate;
  private ReactiveValueOperations<String, Employee> reactiveValueOps;

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
    reactiveValueOps = redisTemplate.opsForValue();
  }

  @Test
  void test() {
    Mono<Boolean> result = reactiveValueOps.set("1", new Employee("1", "Bill", "Accounts"));

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();

    Mono<Employee> fetchedEmployee = reactiveValueOps.get("1");

    StepVerifier.create(fetchedEmployee)
        .expectNext(new Employee("1", "Bill", "Accounts"))
        .verifyComplete();
  }

  @Test
  void test2() throws InterruptedException {
    Mono<Boolean> result = reactiveValueOps.set("2", new Employee("2", "John", "Programming"), Duration.ofSeconds(1));

    Mono<Employee> fetchedEmployee = reactiveValueOps.get("2");

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();

    Thread.sleep(2000L);

    StepVerifier.create(fetchedEmployee)
        .expectNextCount(0L)
        .verifyComplete();
  }
}
