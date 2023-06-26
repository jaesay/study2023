package com.example.webfluxpatterns;

import com.example.webfluxpatterns._10_bulkhead.dto.ProductAggregate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BulkheadTest {

  private WebClient client;

  @BeforeAll
  void setClient() {
    this.client = WebClient.builder()
        .baseUrl("http://localhost:8080/sec10/")
        .build();
  }

  @Test
  void concurrentUsersTest() {
    StepVerifier.create(Flux.merge(fibRequests(), productRequests()))
        .verifyComplete();
  }

  private Mono<Void> fibRequests() {
    return Flux.range(1, 5)
        .flatMap(i -> this.client.get().uri("fib/46").retrieve().bodyToMono(Long.class))
        .doOnNext(this::print)
        .then();
  }

  private Mono<Void> productRequests() {
    return Mono.delay(Duration.ofMillis(100))
        .thenMany(Flux.range(1, 20))
        .flatMap(i -> this.client.get().uri("products/1").retrieve().bodyToMono(ProductAggregate.class))
        .map(ProductAggregate::getCategory)
        .doOnNext(this::print)
        .then();
  }

  private void print(Object o) {
    System.out.println(LocalDateTime.now() + " : " + o);
  }

}
