package com.reactivespring.client;

import com.reactivespring.domain.Review;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
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
        .bodyToFlux(Review.class)
        .log();
  }
}
