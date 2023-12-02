package com.example.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class HelloClient {

  private final WebClient client;
  private final String baseUrl;

  public HelloClient(WebClient webClient, @Value("${client.hello.baseUrl}") String baseUrl) {
    this.client = webClient;
    this.baseUrl = baseUrl;
  }

  public Mono<String> getHello() {
    return client
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder.path("/v1/hello").build())
        .retrieve()
        .bodyToMono(String.class);
  }
}
