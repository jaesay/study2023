package com.example.webfluxpatterns.aggregator.client;

import com.example.webfluxpatterns.aggregator.dto.PromotionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PromotionClient {

  private final WebClient client;

  public PromotionClient(@Value("${client.aggregator.promotion-url}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<PromotionResponse> getPromotion(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .bodyToMono(PromotionResponse.class)
        .log();
  }
}
