package com.example.webclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class HiClient {

  private final WebClient client;
  private final String baseUrl;

  public HiClient(WebClient webClient, @Value("${client.hi.baseUrl}") String baseUrl) {
    this.client = webClient;
    this.baseUrl = baseUrl;
  }

  public Mono<String> getHi() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder.path("/v1/hi").build())
        .retrieve()
        .bodyToMono(String.class);
  }
}
