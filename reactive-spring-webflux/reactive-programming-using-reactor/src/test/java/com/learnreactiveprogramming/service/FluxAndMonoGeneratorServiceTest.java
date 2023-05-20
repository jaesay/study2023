package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

  FluxAndMonoGeneratorService service = new FluxAndMonoGeneratorService();

  @Test
  void namesTest() {
    Flux<String> namesFlux = service.namesFlux();

    StepVerifier.create(namesFlux)
        .expectNext("alex", "ben", "chloe")
        .verifyComplete();
  }

  @Test
  public void namesFlux_flatmap() {
    //given
    int length = 3;

    //when
    Flux<String> namesFluxMap = service.namesFlux_flatmap(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();
  }

  @Test
  void namesFlux_map() {
    int length = 3;

    Flux<String> namesFluxMap = service.namesFlux_map(length);

    StepVerifier.create(namesFluxMap)
        .expectNext("4-ALEX", "5-CHLOE")
        .verifyComplete();
  }

  @Test
  void namesFlux_immutable() {
    Flux<String> namesFlux = service.namesFlux_immutable();

    StepVerifier.create(namesFlux)
        .expectNext("alex", "ben", "chloe")
        .verifyComplete();
  }

  @Test
  void namesFlux_flatmap_async() {
    //given
    int length = 3;

    //when
    Flux<String> namesFluxMap = service.namesFlux_flatmap_async(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNextCount(9)
        .verifyComplete();
  }

  @Test
  void namesFlux_concatmap() {
    //given
    int length = 3;

    //when
    Flux<String> namesFluxMap = service.namesFlux_concatmap(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();
  }

  @Test
  void nameMono_flatMap() {
    //given
    int length = 3;

    //when
    Mono<List<String>> namesMono = service.nameMono_flatMap(length);

    //then
    StepVerifier.create(namesMono)
        .expectNext(List.of("A", "L", "E", "X"))
        .verifyComplete();
  }

  @Test
  void nameMono_flatMapMany() {
    //given
    int length = 3;

    //when
    var namesMono = service.nameMono_flatMapMany(length);

    //then
    StepVerifier.create(namesMono)
        .expectNext("A", "L", "E", "X")
        .verifyComplete();
  }

  @Test
  void namesFlux_transform() {
    //given
    int length = 3;

    //when
    Flux<String> namesFluxMap = service.namesFlux_transform(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNext("A", "L", "E", "X", "C", "H", "L", "O", "E")
        .verifyComplete();
  }

  @Test
  void namesFlux_defaultIfEmpty() {
    //given
    int length = 6;

    //when
    Flux<String> namesFluxMap = service.namesFlux_defaultIfEmpty(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNext("default")
        .verifyComplete();
  }

  @Test
  void namesFlux_switchIfEmpty() {
    //given
    int length = 6;

    //when
    Flux<String> namesFluxMap = service.namesFlux_switchIfEmpty(length);

    //then
    StepVerifier.create(namesFluxMap)
        .expectNext("D", "E", "F", "A", "U", "L", "T")
        .verifyComplete();
  }

  @Test
  void explore_concat() {
    Flux<String> flux = service.explore_concat();

    StepVerifier.create(flux)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  void explore_merge() {
    Flux<String> flux = service.explore_merge();

    StepVerifier.create(flux)
        .expectNext("A", "D", "B", "E", "C", "F")
        .verifyComplete();
  }

  @Test
  void explore_mergeSequential() {
    Flux<String> flux = service.explore_mergeSequential();

    StepVerifier.create(flux)
        .expectNext("A", "B", "C", "D", "E", "F")
        .verifyComplete();
  }

  @Test
  void explore_zip() {
    Flux<String> flux = service.explore_zip();

    StepVerifier.create(flux)
        .expectNext("AD", "BE", "CF")
        .verifyComplete();
  }

  @Test
  void explore_zipWith_mono() {
    Mono<String> mono = service.explore_zipWith_mono();

    StepVerifier.create(mono)
        .expectNext("AB")
        .verifyComplete();
  }
}