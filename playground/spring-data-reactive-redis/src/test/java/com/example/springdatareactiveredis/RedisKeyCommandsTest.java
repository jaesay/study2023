package com.example.springdatareactiveredis;

import com.redis.testcontainers.RedisContainer;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands.SetCommand;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class RedisKeyCommandsTest {

  @Autowired
  private ReactiveKeyCommands keyCommands;

  @Autowired
  private ReactiveStringCommands stringCommands;

  @Container
  static final RedisContainer container = new RedisContainer(
      RedisContainer.DEFAULT_IMAGE_NAME.withTag(RedisContainer.DEFAULT_TAG));

  @DynamicPropertySource
  static void redisProperties(DynamicPropertyRegistry registry) {
    container.start();
    registry.add("spring.redis.url", container::getRedisURI);
  }

  @Test
  void test() {
    Flux<String> keys = Flux.just("key1", "key2", "key3", "key4");

    Flux<SetCommand> generator = keys.map(String::getBytes)
        .map(array -> {
          ByteBuffer key = ByteBuffer.wrap(array);
          return SetCommand.set(key).value(key);
        });

    StepVerifier.create(stringCommands.set(generator))
        .expectNextCount(4L)
        .verifyComplete();

    Mono<Long> keyCount = keyCommands.keys(ByteBuffer.wrap("key*".getBytes()))
        .flatMapMany(Flux::fromIterable)
        .count();

    StepVerifier.create(keyCount)
        .expectNext(4L)
        .verifyComplete();
  }
}
