package com.example.springreactive;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.springreactive.Chapter13Example.BackpressureTestExample;
import com.example.springreactive.Chapter13Example.ContextTestExample;
import com.example.springreactive.Chapter13Example.GeneralTestExample;
import com.example.springreactive.Chapter13Example.RecordTestExample;
import com.example.springreactive.Chapter13Example.TimeBasedTestExample;
import java.time.Duration;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.scheduler.VirtualTimeScheduler;

@DisplayName("CHAPTER 13. Testing")
public class Chapter13ExampleTest {

  @Test
  @DisplayName("StepVerifier 기본 테스트 예제 1")
  void example13_1() {
    StepVerifier
        .create(Mono.just("Hello Reactor")) // 테스트 대상 Sequence 생성
        .expectNext("Hello Reactor")    // emit 된 데이터 검증
        .expectComplete()   // onComplete Signal 검증
        .verify();          // 검증 실행.
  }

  @Test
  @DisplayName("StepVerifier 기본 테스트 예제 2")
  void example13_3() {
    StepVerifier
        .create(GeneralTestExample.sayHello())
        .expectSubscription()
        // as()를 사용해서 이전 기댓값 평가 단계에 대한 설명(description)을 추가할 수 있다.
        .as("# expect subscription")
        .expectNext("Hi")
        .as("# expect Hi")
        .expectNext("Reactor")
        .as("# expect Reactor")
        .verifyComplete();
  }

  @Test
  @DisplayName("StepVerifier 기본 테스트 예제 3")
  void example13_4() {
    Flux<Integer> source = Flux.just(2, 4, 6, 8, 10);
    StepVerifier
        .create(GeneralTestExample.divideByTwo(source))
        .expectSubscription()
        .expectNext(1)
        .expectNext(2)
        .expectNext(3)
        .expectNext(4)
        // .expectNext(1, 2, 3, 4)
        .expectError()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier 기본 테스트 예제 4")
  void example13_5() {
    Flux<Integer> source = Flux.range(0, 1000);
    StepVerifier
        .create(GeneralTestExample.takeNumber(source, 500),
            // StepVerifierOptions는 이름 그대로 StepVerifier에 옵션, 즉 추가적인 기능을 덧붙이는 작업을 하는 클래스이, 예제 코드에서는 테스트에 실패할 경우 팔ㄹㄹ터로 입력한 시나리오명을 출력한다.
            StepVerifierOptions.create().scenarioName("Verify from 0 to 499"))
        .expectSubscription()
        .expectNext(0)
        .expectNextCount(498)
        .expectNext(500)
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier 시간 기반 테스트 예제 1 -  1시간 뒤에 COVID-19 확진자 발생 현황을 체크한다.")
  void example13_7() {
    StepVerifier
        .withVirtualTime(() -> TimeBasedTestExample.getCOVID19Count(
                Flux.interval(Duration.ofHours(1)).take(1)
            )
        )
        .expectSubscription()
        // 기댓값을 평가한 후 then() 메서드를 사용해서 후속 작업을 할 수 있다.
        .then(() -> VirtualTimeScheduler
            .get()
            .advanceTimeBy(Duration.ofHours(1))) // 주어진 시간을 앞당겨서 테스트 한다.
        .expectNextCount(11)
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier 시간 기반 테스트 예제 2")
  void example13_8() {
    StepVerifier
        .create(TimeBasedTestExample.getCOVID19Count(
                Flux.interval(Duration.ofMinutes(1)).take(1)
            )
        )
        .expectSubscription()
        .expectNextCount(11)
        .expectComplete()
        .verify(Duration.ofSeconds(3));
  }

  @Test
  @DisplayName("StepVerifier 시간 기반 테스트 예제 3")
  void example13_9() {
    StepVerifier
        .withVirtualTime(() -> TimeBasedTestExample.getVoteCount(
                Flux.interval(Duration.ofMinutes(1))
            )
        )
        .expectSubscription()
        // expectNoEvent()의 파라미터로 시간을 지정하면 지정한 시간 동안 어떤 이벤트도 발생하지 않을 것이라고 기대하는 도에 지정한 시간만큼 시간을 앞당긴다.
        .expectNoEvent(Duration.ofMinutes(1))
        .expectNoEvent(Duration.ofMinutes(1))
        .expectNoEvent(Duration.ofMinutes(1))
        .expectNoEvent(Duration.ofMinutes(1))
        .expectNoEvent(Duration.ofMinutes(1))
        .expectNextCount(5)
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier Backpressure 테스트 예제 1")
  void example13_11() {
    StepVerifier
        .create(BackpressureTestExample.generateNumber(), 1L)
        .thenConsumeWhile(num -> num >= 1)
        .verifyComplete();
  }

  @Test
  @DisplayName("StepVerifier Backpressure 테스트 예제 2")
  void example13_12() {
    StepVerifier
        .create(BackpressureTestExample.generateNumber(), 1L)
        .thenConsumeWhile(num -> num >= 1)
        .expectError()
        .verifyThenAssertThat()
        .hasDroppedElements();
  }

  @Test
  @DisplayName("StepVerifier Context 테스트 예제 1")
  void example13_14() {
    Mono<String> source = Mono.just("hello");

    StepVerifier
        .create(
            ContextTestExample
                .getSecretMessage(source)
                .contextWrite(context ->
                    context.put("secretMessage", "Hello, Reactor"))
                .contextWrite(context -> context.put("secretKey", "aGVsbG8="))
        )
        .expectSubscription()
        .expectAccessibleContext()
        .hasKey("secretKey")
        .hasKey("secretMessage")
        .then()
        .expectNext("Hello, Reactor")
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier Record 테스트 예제 1")
  void example13_16() {
    StepVerifier
        .create(RecordTestExample.getCapitalizedCountry(
            Flux.just("korea", "england", "canada", "india")))
        .expectSubscription()
        .recordWith(ArrayList::new)
        .thenConsumeWhile(country -> !country.isEmpty())
        .consumeRecordedWith(countries ->
            assertThat(countries
                .stream()
                .allMatch(country ->
                    Character.isUpperCase(country.charAt(0)))).isTrue())
        .expectComplete()
        .verify();
  }

  @Test
  @DisplayName("StepVerifier Record 테스트 예제 2")
  void example13_17() {
    StepVerifier
        .create(RecordTestExample.getCapitalizedCountry(
            Flux.just("korea", "england", "canada", "india")))
        .expectSubscription()
        .recordWith(ArrayList::new)
        .thenConsumeWhile(country -> !country.isEmpty())
        .expectRecordedMatches(countries ->
            countries
                .stream()
                .allMatch(country ->
                    Character.isUpperCase(country.charAt(0))))
        .expectComplete()
        .verify();
  }
}