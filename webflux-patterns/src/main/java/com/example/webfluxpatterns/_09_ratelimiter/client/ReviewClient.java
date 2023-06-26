package com.example.webfluxpatterns._09_ratelimiter.client;

import com.example.webfluxpatterns._09_ratelimiter.dto.Review;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${sec09.review.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  @RateLimiter(name = "review-service", fallbackMethod = "fallback")
  public Mono<List<Review>> getReviews(long id){
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> Mono.empty())
        .bodyToFlux(Review.class)
        .collectList(); // doOnNext(list -> put in cache..
  }

  public Mono<List<Review>> fallback(long id, Throwable ex){
    // fromSupplier(list -> read from cache..
    return Mono.just(Collections.emptyList());
  }

}
