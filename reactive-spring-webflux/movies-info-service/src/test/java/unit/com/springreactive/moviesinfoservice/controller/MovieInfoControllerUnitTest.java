package com.springreactive.moviesinfoservice.controller;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;

@WebFluxTest(controllers = MovieInfoController.class)
class MovieInfoControllerUnitTest {

  @Autowired
  WebTestClient webTestClient;

  @MockBean
  MovieInfoService movieInfoService;

  static final String MOVIE_INFO_URI = "/v1/movieinfos";

  @Test
  void getAllMovieInfos() {
    var movieInfos = List.of(new MovieInfo(null, "Batman Begins",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
        new MovieInfo(null, "The Dark Knight",
            2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
        new MovieInfo("abc", "Dark Knight Rises",
            2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

    given(movieInfoService.getAllMovieInfos()).willReturn(Flux.fromIterable(movieInfos));

    webTestClient
        .get()
        .uri(MOVIE_INFO_URI)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(MovieInfo.class)
        .hasSize(3);
  }

  @Test
  void getMovieInfoById() {
    String id = "abc";
    MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises",
        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
    given(movieInfoService.getMovieInfoById(id)).willReturn(Mono.just(movieInfo));

    webTestClient
        .get()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.movieInfoId").isEqualTo(id);
  }

  @Test
  void addMovieInfo() {
    MovieInfo movieInfo = new MovieInfo("mockId", "Batman Begins1",
        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
    given(movieInfoService.addMovieInfo(isA(MovieInfo.class))).willReturn(Mono.just(movieInfo));

    webTestClient
        .post()
        .uri(MOVIE_INFO_URI)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assertThat(savedMovieInfo).isNotNull();
          assertThat(savedMovieInfo.getMovieInfoId()).isEqualTo("mockId");
        });
  }

  @Test
  void addMovieInfo_validation() {
    MovieInfo movieInfo = new MovieInfo("mockId", "",
        -2005, List.of(""), LocalDate.parse("2005-06-15"));

    webTestClient
        .post()
        .uri(MOVIE_INFO_URI)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody(String.class)
        .consumeWith(stringEntityExchangeResult -> {
          String responseBody = stringEntityExchangeResult.getResponseBody();
          assertThat(responseBody).isEqualTo("movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a positive value");
        });
  }

  @Test
  void updateMovieInfo() {
    String id = "abc";
    MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises1",
        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

    given(movieInfoService.updateMovieInfo(isA(MovieInfo.class), isA(String.class))).willReturn(Mono.just(movieInfo));

    webTestClient
        .put()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .bodyValue(movieInfo)
        .exchange()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assertThat(savedMovieInfo).isNotNull();
          assertThat(savedMovieInfo.getMovieInfoId()).isEqualTo(id);
          assertThat(savedMovieInfo.getName()).isEqualTo("Dark Knight Rises1");
        });
  }

  @Test
  void deleteMovieInfo() {
    String id = "abc";
    given(movieInfoService.deleteMovieInfo(isA(String.class))).willReturn(Mono.empty());

    webTestClient
        .delete()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .exchange()
        .expectStatus()
        .isNoContent();
  }
}