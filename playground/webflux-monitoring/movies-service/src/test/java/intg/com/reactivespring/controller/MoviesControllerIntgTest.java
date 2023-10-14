package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(properties = {
    "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
    "restClient.reviewsUrl=http://localhost:8084/v1/reviews"
})
class MoviesControllerIntgTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void retrieveMovieById() {
    String movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
        .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("movieinfo.json")
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("reviews.json")
        ));

    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Movie.class)
        .consumeWith(movieEntityExchangeResult -> {
          Movie movie = movieEntityExchangeResult.getResponseBody();
          assertThat(movie).isNotNull();
          assertThat(movie.getMovieInfo()).isNotNull();
          assertThat(movie.getMovieInfo().getName()).isEqualTo("Batman Begins");
          assertThat(movie.getReviewList()).hasSize(2);
        });
  }

  @Test
  void retrieveMovieById_movieinfos_404() {
    String movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value())
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("reviews.json")
        ));

    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(String.class)
        .isEqualTo("There is no MovieInfo available for the passed in id : abc");

    WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
  }

  @Test
  void retrieveMovieById_reviews_404() {
    String movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
        .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("movieinfo.json")
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.NOT_FOUND.value())
        ));

    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Movie.class)
        .consumeWith(movieEntityExchangeResult -> {
          Movie movie = movieEntityExchangeResult.getResponseBody();
          assertThat(movie).isNotNull();
          assertThat(movie.getMovieInfo()).isNotNull();
          assertThat(movie.getMovieInfo().getName()).isEqualTo("Batman Begins");
          assertThat(movie.getReviewList()).hasSize(0);
        });
  }

  @Test
  void retrieveMovieById_movieinfos_5XX() {
    String movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
            .withBody("MoviesInfo Service Unavailable")
        ));

    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().is5xxServerError()
        .expectBody(String.class)
        .isEqualTo("Server exception in MoviesInfoService : MoviesInfo Service Unavailable");

    WireMock.verify(4, getRequestedFor(urlEqualTo("/v1/movieinfos/" + movieId)));
  }

  @Test
  void retrieveMovieById_reviews_5XX() {
    String movieId = "abc";
    stubFor(get(urlEqualTo("/v1/movieinfos/" + movieId))
        .willReturn(aResponse()
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .withBodyFile("movieinfo.json")
        ));

    stubFor(get(urlPathEqualTo("/v1/reviews"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
            .withBody("Reviews Service Unavailable")
        ));

    webTestClient
        .get()
        .uri("/v1/movies/{id}", movieId)
        .exchange()
        .expectStatus().is5xxServerError()
        .expectBody(String.class)
        .isEqualTo("Server exception in ReviewsService : Reviews Service Unavailable");

    WireMock.verify(4, getRequestedFor(urlPathEqualTo("/v1/reviews")));
  }
}