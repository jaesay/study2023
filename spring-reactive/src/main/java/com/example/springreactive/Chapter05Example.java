package com.example.springreactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class Chapter05Example {

  /**
   * Hello 코드로 보는 Reactor의 구성요소
   */
  private static void example5_1() {
    Flux<String> sequence = Flux.just("Hello", "Reactor"); // 데이터를 생성해서 제공
    sequence.map(String::toLowerCase) // 데이터를 가공
        .subscribe(System.out::println); // 전달받은 데이터를 처리
  }

  public static void main(String[] args) {
    example5_1();
  }
}
