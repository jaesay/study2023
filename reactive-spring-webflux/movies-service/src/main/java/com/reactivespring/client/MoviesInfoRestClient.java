package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class MoviesInfoRestClient {

  private final WebClient webClient;
  private final String moviesInfoUrl;

  public MoviesInfoRestClient(WebClient webClient, @Value("${restClient.moviesInfoUrl}") String moviesInfoUrl) {
    this.webClient = webClient;
    this.moviesInfoUrl = moviesInfoUrl;
  }

  public Mono<MovieInfo> getMovieInfo(String movieId) {
    String url = moviesInfoUrl.concat("/{id}");
    return webClient
        .get()
        .uri(url, movieId)
        .retrieve()
        .bodyToMono(MovieInfo.class)
        .log();
  }
}
