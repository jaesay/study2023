package com.example.webflux;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class SampleServiceTest {

  SampleService service = new SampleService();

  @Test
  void getSample_1() {
    StepVerifier.create(service.getSample_1())
        .expectNext(new Sample("prop1_1", "prop1_2", "prop2_1", "prop2_2", "prop3"))
        .verifyComplete();
  }

  @Test
  void getSample_2() {
    StepVerifier.create(service.getSample_2())
        .expectNext(new Sample("prop1_1", "prop1_2", "prop2_1", "prop2_2", "prop3"))
        .verifyComplete();
  }
}