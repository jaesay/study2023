package com.example.webfluxpatterns._06_timeout.client;

import com.example.webfluxpatterns._06_timeout.dto.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {

  private final WebClient client;

  public ProductClient(@Value("${sec06.product.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<Product> getProduct(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .bodyToMono(Product.class)
        .log()
        .onErrorResume(ex -> Mono.empty());
  }
}
