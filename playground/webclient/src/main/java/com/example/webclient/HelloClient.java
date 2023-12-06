package com.example.webclient;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HelloClient {

  private final WebClient client;
  private final String baseUrl;

  public HelloClient(WebClient webClient, @Value("${client.hello.baseUrl}") String baseUrl) {
    this.client = webClient;
    this.baseUrl = baseUrl;
  }

  @TimeLimiter(name = "hello", fallbackMethod = "fallback")
  public Mono<String> getHello() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder.path("/v1/hello").build())
        .retrieve()
        .bodyToMono(String.class);
  }

  private Mono<String> fallback(Throwable throwable) {
    log.error(throwable.getMessage());
    return Mono.just("Hi");
  }
}
