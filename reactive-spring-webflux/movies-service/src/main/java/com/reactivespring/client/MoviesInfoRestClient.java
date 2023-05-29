package com.reactivespring.client;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
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
        .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
          log.info("Status code is : {}", clientResponse.statusCode().value());
          if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
            return Mono.error(new MoviesInfoClientException(
                "There is no MovieInfo available for the passed in id : " + movieId,
                clientResponse.statusCode().value()
            ));
          }

          return clientResponse.bodyToMono(String.class)
              .flatMap(responseBody -> Mono.error(new MoviesInfoClientException(
                  responseBody, clientResponse.statusCode().value()
              )));
        })
        .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
          log.info("Status code is : {}", clientResponse.statusCode().value());

          return clientResponse.bodyToMono(String.class)
              .flatMap(responseBody -> Mono.error(new MoviesInfoServerException(
                  "Server exception in MoviesInfoService : " + responseBody)));
        })
        .bodyToMono(MovieInfo.class)
        .log();
  }
}
