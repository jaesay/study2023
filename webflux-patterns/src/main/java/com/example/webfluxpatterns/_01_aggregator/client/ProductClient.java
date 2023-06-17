package com.example.webfluxpatterns._01_aggregator.client;

import com.example.webfluxpatterns._01_aggregator.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient client;

  public ProductClient(@Value("${client.aggregator.product-url}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<ProductResponse> getProduct(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .bodyToMono(ProductResponse.class)
        .log()
        .onErrorResume(ex -> Mono.empty());
  }
}
