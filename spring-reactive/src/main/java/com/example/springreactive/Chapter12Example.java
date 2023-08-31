package com.example.springreactive;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.scheduler.Schedulers;

/**
 * CHAPTER 12. Debugging
 */
@Slf4j
public class Chapter12Example {

  public static Map<String, String> fruits = new HashMap<>();

  static {
    fruits.put("banana", "바나나");
    fruits.put("apple", "사과");
    fruits.put("pear", "배");
    fruits.put("grape", "포도");
  }

  /**
   * onOperatorDebug() Hook 메서드를 이용한 Debug mode 예제
   *   - 애플리케이션 전체에서 global 하게 동작한다.
   */
  private static void example12_1() throws InterruptedException {
    // 디버그 모드를 활성화하면 애플리케이션 내에 있는 모든 Operator의 스택트레이스를 캡처하므로 프로덕션 환경에서는 사용하지 않아야 한다.
    // Reactor Tools에서 지원하는 ReactorDebugAgent를 사용하여 프로덕션 환경에서 디버그모드를 대체할 수 있다.
    // 코드상의 선언 없이 IntellJ에서 설정으로 해당 기능을 이용할 수 있다.
    Hooks.onOperatorDebug();

    Flux
        .fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
        .subscribeOn(Schedulers.boundedElastic())
        .publishOn(Schedulers.parallel())
        .map(String::toLowerCase)
        .map(fruit -> fruit.substring(0, fruit.length() - 1))
        .map(fruits::get)
        .map(translated -> "맛있는 " + translated)
        .subscribe(
            log::info,
            error -> log.error("# onError:", error));

    Thread.sleep(100L);
  }

  /**
   * checkpoint()를 사용한 디버깅 예
   *   - checkpoint()를 지정한 Operator 체인에서만 동작한다.
   */
  private static void example12_2() {
    Flux
        .just(2, 4, 6, 8)
        .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x / y)
        .map(num -> num + 2)
        .checkpoint()
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error)
        );
  }

  /**
   * checkpoint()를 사용한 디버깅 예
   *   - checkpoint()를 지정한 Operator 체인에서만 동작한다.
   */
  private static void example12_3() {
    Flux
        .just(2, 4, 6, 8)
        .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x / y)
        .checkpoint()
        .map(num -> num + 2)
        .checkpoint()
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error)
        );
  }

  /**
   * checkpoint(description)을 사용한 디버깅 예
   *   - description 을 추가해서 에러가 발생한 지점을 구분할 수 있다.
   *   - description 을 지정할 경우 traceback 을 추가하지 않는다.
   */
  private static void example12_4() {
    Flux
        .just(2, 4, 6, 8)
        .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x / y)
        .checkpoint("example12_4.zipWith.checkpoint")
        .map(num -> num + 2)
        .checkpoint("example12_4.map.checkpoint")
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error)
        );
  }

  /**
   * checkpoint(description)을 사용한 디버깅 예
   *   - description 을 추가해서 에러가 발생한 지점을 구분할 수 있다.
   *   - forceStackTrace을 true로 지정할 경우 traceback도 추가한다.
   */
  private static void example12_5() {
    Flux
        .just(2, 4, 6, 8)
        .zipWith(Flux.just(1, 2, 3, 0), (x, y) -> x / y)
        .checkpoint("example12_4.zipWith.checkpoint", true)
        .map(num -> num + 2)
        .checkpoint("example12_4.map.checkpoint", true)
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error)
        );
  }

  /**
   * 복잡한 단계를 거치는 Operator 체인에서 checkpoint()를 사용하는 예제
   */
  private static void example12_6() {
    Flux<Integer> source = Flux.just(2, 4, 6, 8);
    Flux<Integer> other = Flux.just(1, 2, 3, 0);

    Flux<Integer> multiplySource = divide(source, other).checkpoint();
    Flux<Integer> plusSource = plus(multiplySource).checkpoint();

    plusSource.subscribe(
        data -> log.info("# onNext: {}", data),
        error -> log.error("# onError:", error)
    );
  }

  private static Flux<Integer> divide(Flux<Integer> source, Flux<Integer> other) {
    return source.zipWith(other, (x, y) -> x / y);
  }

  private static Flux<Integer> plus(Flux<Integer> source) {
    return source.map(num -> num + 2);
  }

  /**
   * log() Operator를 사용한 예제
   */
  private static void example12_7() {
    Flux.fromArray(new String[]{"BANANAS", "APPLES", "PEARS", "MELONS"})
        .map(String::toLowerCase)
        .map(fruit -> fruit.substring(0, fruit.length() - 1))
//        .log()
        .log("Fruit.Substring", Level.FINE)
        .map(fruits::get)
        .subscribe(
            log::info,
            error -> log.error("# onError:", error));
  }

  public static void main(String[] args) throws InterruptedException {
    example12_7();
  }

}
