package com.springreactive.moviesinfoservice.repository;

import com.springreactive.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest(properties = "spring.mongodb.embedded.version=3.2.2")
@ActiveProfiles("test")
class MovieInfoRepositoryTest {

  @Autowired
  MovieInfoRepository movieInfoRepository;

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
  void findAll() {
    Flux<MovieInfo> flux = movieInfoRepository.findAll().log();

    StepVerifier.create(flux)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void findById() {
    Mono<MovieInfo> mono = movieInfoRepository.findById("abc").log();

    StepVerifier.create(mono)
        .assertNext(movieInfo -> {
          assertThat(movieInfo.getName()).isEqualTo("Dark Knight Rises");
        })
        .verifyComplete();
  }

  @Test
  void saveMovieInfo() {
    Mono<MovieInfo> mono = movieInfoRepository.save(new MovieInfo(null, "Batman Begins1",
        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"))).log();

    StepVerifier.create(mono)
        .assertNext(movieInfo -> {
          assertThat(movieInfo.getMovieInfoId()).isNotNull();
          assertThat(movieInfo.getName()).isEqualTo("Batman Begins1");
        })
        .verifyComplete();
  }

  @Test
  void updateMovieInfo() {
    MovieInfo movieInfo = movieInfoRepository.findById("abc").log().block();
    movieInfo.setYear(2021);

    Mono<MovieInfo> mono = movieInfoRepository.save(movieInfo).log();

    StepVerifier.create(mono)
        .assertNext(updatedMovieInfo -> {
          assertThat(updatedMovieInfo.getYear()).isEqualTo(2021);
        })
        .verifyComplete();
  }

  @Test
  void deleteMovieInfo() {
    movieInfoRepository.deleteById("abc").log().block();
    Flux<MovieInfo> flux = movieInfoRepository.findAll().log();

    StepVerifier.create(flux)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void findByYear() {
    Flux<MovieInfo> flux = movieInfoRepository.findByYear(2005).log();

    StepVerifier.create(flux)
        .expectNextCount(1)
        .verifyComplete();
  }
}