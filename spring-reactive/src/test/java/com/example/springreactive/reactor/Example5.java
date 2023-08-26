package com.example.springreactive.reactor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class Example5 {

  @Test
  @DisplayName("5-1. Hello 코드로 보는 Reactor의 구성요소")
  void example5_1() {
    Flux.just("Hello", "Reactor") // 데이터를 생성해서 제공
        .map(String::toLowerCase) // 데이터를 가공
        .subscribe(System.out::println); // 전달받은 데이터를 처리
  }

}
