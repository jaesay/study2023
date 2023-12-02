package com.example.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WorldClient {

  private final WebClient client;
  private final String baseUrl;

  public WorldClient(WebClient worldWebClient,
      @Value("${client.world.baseUrl}") String baseUrl) {
    this.client = worldWebClient;
    this.baseUrl = baseUrl;
  }

  public Mono<String> getWorld() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder
            .path("/v1/world")
            .build())
        .retrieve()
        .bodyToMono(String.class);
  }
}
