package com.example.springreactive.reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class Example6 {

  @Test
  @DisplayName("6-1. Mono 기본 개념 예제 - 1개의 데이터를 생성해서 emit한다.")
  void example6_1() {
    Mono.just("Hello Reactor")
        .subscribe(System.out::println);
  }
}
