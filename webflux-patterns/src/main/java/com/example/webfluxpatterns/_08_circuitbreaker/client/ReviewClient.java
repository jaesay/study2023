package com.example.webfluxpatterns._08_circuitbreaker.client;

import com.example.webfluxpatterns._08_circuitbreaker.dto.Review;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ReviewClient {

  private final WebClient client;

  public ReviewClient(@Value("${sec08.review.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  @CircuitBreaker(name = "review-service", fallbackMethod = "fallback")
  public Mono<List<Review>> getReviews(long id) {
    return this.client
        .get()
        .uri("{id}", id)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> Mono.empty())
        .bodyToFlux(Review.class)
        .collectList()
        .retry(5)
        .timeout(Duration.ofMillis(200)); // 모든 retry 합한 시간에 대한 timeout, 타임아웃 내에 이뤄지지 못하면 WebClientResponseException가 아닌 TimeoutException이 발생하기 때문에 요것도 record에 추가해줘야 함
//        .onErrorReturn(Collections.emptyList()); // circuitbreaker가 에러를 감지하지 못함(error signal X)
  }

  public Mono<List<Review>> fallback(long id, Throwable ex) {
    // fallback reviews called : Did not observe any item or terminal signal within 200ms in 'retry' (and no fallback has been configured)
    // timeout에 fallback이 설정되어 있지 않고 circuitbreaker에서 에러를 전달받기 때문에 위와 같은 로그가 출력됨
    log.error("fallback reviews called : {}", ex.getMessage());
    return Mono.just(Collections.emptyList());
  }
}
