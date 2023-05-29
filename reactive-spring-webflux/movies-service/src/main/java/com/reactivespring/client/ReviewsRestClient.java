package com.reactivespring.client;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewsClientException;
import com.reactivespring.exception.ReviewsServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class ReviewsRestClient {

  private final WebClient webClient;
  private final String reviewsUrl;

  public ReviewsRestClient(WebClient webClient, @Value("${restClient.reviewsUrl}") String reviewsUrl) {
    this.webClient = webClient;
    this.reviewsUrl = reviewsUrl;
  }

  public Flux<Review> getReviews(String movieId) {
    String url = UriComponentsBuilder.fromHttpUrl(reviewsUrl)
        .queryParam("movieInfoId", movieId)
        .toUriString();

    return webClient
        .get()
        .uri(url)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
          log.info("Status code is : {}", clientResponse.statusCode().value());
          if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
            return Mono.empty();
          }

          return clientResponse.bodyToMono(String.class)
              .flatMap(responseBody -> Mono.error(new ReviewsClientException(responseBody)));
        })
        .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
          log.info("Status code is : {}", clientResponse.statusCode().value());

          return clientResponse.bodyToMono(String.class)
              .flatMap(responseBody -> Mono.error(new ReviewsServerException(
                  "Server exception in ReviewsService : " + responseBody)));
        })
        .bodyToFlux(Review.class)
        .log();
  }
}
