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

  @Test
  @DisplayName("6-2. Mono 기본 개념 예제 - 원본 데이터의 emit 없이 onComplete signal 만 emit 한다.")
  void example6_2() {
    Mono
        .empty() // emit할 데이터가 없는 것으로 간주하여 곧바로 onComplete Signal을 전송한다. 어떤 특정 작업을 통해 데이터를 전달받을 필요는 없지만 작업이 끝났음을 알리고 이에 따른 후처리를 하고 싶을 때 사용할 수 있다.
        .doOnNext(none -> System.out.println("Operator 실행 안됨"))
        .subscribe(
            none -> System.out.println("Publisher가 onNext Signal을 전송하면 실행"),
            error -> System.out.println("Publisher가 onError Signal을 전송하면 실행"),
            () -> System.out.println("Publisher가 onComplete Signal을 전송하면 실행")
        );

  }
}
