package com.springreactive.moviesinfoservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(controllers = FluxAndMonoController.class)
class FluxAndMonoControllerTest {

  @Autowired
  WebTestClient webTestClient;

  @Test
  void flux() {

    webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBodyList(Integer.class).hasSize(3);
  }

  @Test
  void flux_approach2() {

    Flux<Integer> flux = webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .returnResult(Integer.class)
        .getResponseBody();

    StepVerifier.create(flux)
        .expectNext(1, 2, 3)
        .verifyComplete();
  }

  @Test
  void flux_approach3() {

    webTestClient
        .get()
        .uri("/flux")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBodyList(Integer.class)
        .consumeWith(listEntityExchangeResult -> {
          List<Integer> responseBody = listEntityExchangeResult.getResponseBody();
          assertThat(responseBody).isNotNull().hasSize(3);
        });
  }

  @Test
  void mono() {

    webTestClient
        .get()
        .uri("/mono")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .expectBody(Integer.class)
        .consumeWith(integerEntityExchangeResult -> {
          Integer responseBody = integerEntityExchangeResult.getResponseBody();
          assertThat(responseBody).isEqualTo(1);
        });
  }

  @Test
  void stream() {

    Flux<Long> flux = webTestClient
        .get()
        .uri("/stream")
        .exchange()
        .expectStatus().is2xxSuccessful()
        .returnResult(Long.class)
        .getResponseBody();

    StepVerifier.create(flux)
        .expectNext(0L, 1L, 2L, 3L)
        .thenCancel()
        .verify();
  }
}