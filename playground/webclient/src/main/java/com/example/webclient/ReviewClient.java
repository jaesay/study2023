package com.example.webclient;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ReviewClient {

  private final WebClient webClient;
  private final String baseUrl;
  private static final ParameterizedTypeReference<List<ReviewDto>> typeRef = new ParameterizedTypeReference<>() {
  };

  public ReviewClient(WebClient reviewWebClient,
      @Value("${client.review.baseUrl}") String baseUrl) {
    this.webClient = reviewWebClient;
    this.baseUrl = baseUrl;
  }

  public Mono<List<ReviewDto>> getReviews(long productId) {
    return webClient
        .get()
        .uri(baseUrl, uriBuilder -> uriBuilder
            .path("/v1/reviews")
            .queryParam("productId", productId)
            .build())
        .retrieve()
        .bodyToMono(typeRef);
  }
}
