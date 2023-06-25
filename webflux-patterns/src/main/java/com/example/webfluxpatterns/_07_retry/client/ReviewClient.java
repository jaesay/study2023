package com.example.webfluxpatterns._07_retry.client;

import com.example.webfluxpatterns._07_retry.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${sec07.review.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<List<Review>> getReviews(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .bodyToFlux(Review.class)
        .collectList()
        .log()
        .onErrorReturn(Collections.emptyList());
  }
}
