package com.springreactive.moviesinfoservice.controller;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import com.springreactive.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = "spring.mongodb.embedded.version=3.2.2"
)
@ActiveProfiles("test")
class MovieInfoControllerTest {

  @Autowired
  MovieInfoRepository movieInfoRepository;

  @Autowired
  WebTestClient webTestClient;

  static final String MOVIE_INFO_URI = "/v1/movieinfos";

  @BeforeEach
  void setUp() {

    var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
            2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
        new MovieInfo(null, "The Dark Knight",
            2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
        new MovieInfo("abc", "Dark Knight Rises",
            2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

    movieInfoRepository.saveAll(movieinfos)
        .blockLast();
  }

  @AfterEach
  void tearDown() {
    movieInfoRepository.deleteAll().block();
  }

  @Test
  void getAllMovieInfos() {

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
  void getMovieInfosByYear() {
    var uri = UriComponentsBuilder.fromUriString(MOVIE_INFO_URI)
        .queryParam("year", 2005)
        .toUriString();

    webTestClient
        .get()
        .uri(uri)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(MovieInfo.class)
        .hasSize(1);
  }

  @Test
  void getMovieInfoById() {
    String movieInfoId = "abc";

    webTestClient
        .get()
        .uri(MOVIE_INFO_URI + "/{id}", movieInfoId)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Dark Knight Rises");
  }

  @Test
  void getMovieInfoById_notfound() {
    String movieInfoId = "def";

    webTestClient
        .get()
        .uri(MOVIE_INFO_URI + "/{id}", movieInfoId)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void addMovieInfo() {
    MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1",
        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

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
          assertThat(savedMovieInfo.getMovieInfoId()).isNotNull();
        });
  }

  @Test
  void streamMovieInfos() {
    //given
    MovieInfo movieInfo = new MovieInfo(null, "Batman Begins1",
        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

    webTestClient
        .post()
        .uri(MOVIE_INFO_URI)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isCreated();

    //when
    Flux<MovieInfo> flux = webTestClient
        .get()
        .uri(MOVIE_INFO_URI + "/stream")
        .exchange()
        .expectStatus()
        .isOk()
        .returnResult(MovieInfo.class)
        .getResponseBody();

    //then
    StepVerifier.create(flux)
        .assertNext(newMovieInfo -> {
          assertThat(newMovieInfo.getMovieInfoId()).isNotNull();
        })
        .thenCancel()
        .verify();
  }

  @Test
  void updateMovieInfo() {
    String id = "abc";
    MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises1",
        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

    webTestClient
        .put()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(MovieInfo.class)
        .consumeWith(movieInfoEntityExchangeResult -> {
          MovieInfo savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
          assertThat(savedMovieInfo).isNotNull();
          assertThat(savedMovieInfo.getMovieInfoId()).isEqualTo(id);
          assertThat(savedMovieInfo.getName()).isEqualTo("Dark Knight Rises1");
        });
  }

  @Test
  void updateMovieInfo_notfound() {
    String id = "def";
    MovieInfo movieInfo = new MovieInfo("abc", "Dark Knight Rises1",
        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));

    webTestClient
        .put()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .bodyValue(movieInfo)
        .exchange()
        .expectStatus()
        .isNotFound();
  }

  @Test
  void deleteMovieInfo() {
    String id = "abc";

    webTestClient
        .delete()
        .uri(MOVIE_INFO_URI + "/{id}", id)
        .exchange()
        .expectStatus()
        .isNoContent();
  }
}