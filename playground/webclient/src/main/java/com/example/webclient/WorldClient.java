package com.example.webclient;

import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WorldClient {

  private final WebClient client;
  private final String baseUrl;

  public WorldClient(WebClient webClient2,
      @Value("${client.world.baseUrl}") String baseUrl) {
    this.client = webClient2;
    this.baseUrl = baseUrl;
  }

  // resilience4j-reactor(TimeLimiterOperator)를 통해 timeout() operator가 감싸지게 된다.
  @TimeLimiter(name = "world")
  public Mono<String> getWorld() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder
            .path("/v1/world")
            .build())
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> getWorld2() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder
            .path("/v1/world")
            .build())
        .retrieve()
        .bodyToMono(String.class)
        .timeout(Duration.ofSeconds(1));
  }
}
