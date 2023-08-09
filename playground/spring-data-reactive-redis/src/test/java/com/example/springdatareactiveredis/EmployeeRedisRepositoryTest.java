package com.example.springdatareactiveredis;

import com.redis.testcontainers.RedisContainer;
import java.util.List;
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
class EmployeeRedisRepositoryTest {

  @Autowired
  private EmployeeRedisRepository repository;

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
  void findByIdTest() throws InterruptedException {
    Mono<Boolean> result = reactiveValueOps.set("employee:1", new Employee("1", "Bill", "Accounts"));

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();

    Mono<Employee> employee = repository.findById("1");

    StepVerifier.create(employee)
        .expectNext(new Employee("1", "Bill", "Accounts"))
        .verifyComplete();
  }

  @Test
  void findAllByIdTest() {
    Mono<Boolean> result = reactiveValueOps.set("employee:1", new Employee("1", "Bill", "Accounts"));

    StepVerifier.create(result)
        .expectNext(true)
        .verifyComplete();

    Mono<Boolean> result2 = reactiveValueOps.set("employee:2", new Employee("2", "John", "Programming"));

    StepVerifier.create(result2)
        .expectNext(true)
        .verifyComplete();

    Mono<List<Employee>> employees = repository.findAllById(List.of("1", "2"));

    StepVerifier.create(employees)
        .expectNext(List.of(new Employee("1", "Bill", "Accounts"), new Employee("2", "John", "Programming")))
        .verifyComplete();
  }

}