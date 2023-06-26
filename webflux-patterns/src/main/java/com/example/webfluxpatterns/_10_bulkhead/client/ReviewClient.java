package com.example.webfluxpatterns._10_bulkhead.client;

import com.example.webfluxpatterns._10_bulkhead.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${sec10.review.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<List<Review>> getReviews(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> Mono.empty())
        .bodyToFlux(Review.class)
        .collectList();
  }

}
