package com.example.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient webClient;
  private final String baseUrl;

  public ProductClient(WebClient webClient, @Value("${client.product.baseUrl}") String baseUrl) {
    this.webClient = webClient;
    this.baseUrl = baseUrl;
  }

  public Mono<ProductDto> getProduct(long productId) {
    return webClient
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder
            .path("/v1/products/{productId}")
            .build(productId))
        .retrieve()
        .bodyToMono(ProductDto.class);
  }
}
