package com.example.webfluxpatterns._08_circuitbreaker.client;

import com.example.webfluxpatterns._08_circuitbreaker.dto.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${sec08.review.service}") String baseUrl){
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
        .collectList()
        .log()
        .retry(5)
//        .retryWhen(Retry.fixedDelay(5, Duration.ofSeconds(1)))
        .timeout(Duration.ofMillis(200)) // 모든 retry 합한 시간에 대한 timeout
        .onErrorReturn(Collections.emptyList());
  }
}
