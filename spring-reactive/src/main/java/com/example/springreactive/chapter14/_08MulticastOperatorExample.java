package com.example.springreactive.chapter14;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

/**
 * CHATER 14.9. 다수의 Subscriber에게 Flux를 멀티캐스팅(multicasting)하기 위한 Operator
 */
@Slf4j
public class _08MulticastOperatorExample {

  /**
   * publish 예제 1
   *  - publish() Operator
   *      - 다수의 Subscriber와 Flux를 공유한다.
   *      - 즉, Cold Sequence를 Hot Sequence로 변환한다.
   *      - connect()가 호출 되기 전까지는 데이터를 emit하지 않는다.
   */
  private static void example14_60() throws InterruptedException {
    ConnectableFlux<Integer> flux =
        Flux
            .range(1, 5)
            .delayElements(Duration.ofMillis(300L))
            .publish();

    Thread.sleep(500L);
    flux.subscribe(data -> log.info("# subscriber1: {}", data));

    Thread.sleep(200L);
    flux.subscribe(data -> log.info("# subscriber2: {}", data));

    flux.connect();

    Thread.sleep(1000L);
    flux.subscribe(data -> log.info("# subscriber3: {}", data));

    Thread.sleep(2000L);
  }

  private static ConnectableFlux<String> publisher;
  private static int checkedAudience;
  static {
    publisher =
        Flux
            .just("Concert part1", "Concert part2", "Concert part3")
            .delayElements(Duration.ofMillis(300L))
            .publish();
  }

  /**
   * publish() 예제
   */
  private static void example14_61() throws InterruptedException {
    checkAudience();
    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 1 is watching {}", data));
    checkedAudience++;

    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 2 is watching {}", data));
    checkedAudience++;

    checkAudience();

    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 3 is watching {}", data));

    Thread.sleep(1000L);
  }

  public static void checkAudience() {
    if (checkedAudience >= 2) {
      publisher.connect();
    }
  }

  /**
   * autoConnect 예제
   *   - 다수의 Subscriber와 Flux를 공유한다.
   *   - 즉, Cold Sequence를 Hot Sequence로 변환한다.
   *   - 파라미터로 입력한 숫자만큼의 구독이 발생하는 시점에 connect()가 자동으로 호출된다.
   */
  private static void example14_62() throws InterruptedException {
    Flux<String> publisher =
        Flux
            .just("Concert part1", "Concert part2", "Concert part3")
            .delayElements(Duration.ofMillis(300L))
            .publish()
            .autoConnect(2);

    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 1 is watching {}", data));

    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 2 is watching {}", data));

    Thread.sleep(500L);
    publisher.subscribe(data -> log.info("# audience 3 is watching {}", data));

    Thread.sleep(1000L);
  }

  /**
   * refCount() 예제
   *   - 다수의 Subscriber와 Flux를 공유한다.
   *   - 즉, Cold Sequence를 Hot Sequence로 변환한다.
   *   - 파라미터로 입력한 숫자만큼의 구독이 발생하는 시점에 connect()가 자동으로 호출된다.
   *   - 모든 구독이 취소되면 Upstream 소스와의 연결을 해제한다.
   */
  private static void example14_63() throws InterruptedException {
    Flux<Long> publisher =
        Flux
            .interval(Duration.ofMillis(500))
//            .publish().autoConnect(1);
            .publish().refCount(1);

    Disposable disposable =
        publisher.subscribe(data -> log.info("# subscriber 1: {}", data));

    Thread.sleep(2100L);
    disposable.dispose();

    publisher.subscribe(data -> log.info("# subscriber 2: {}", data));

    Thread.sleep(2500L);
  }

  public static void main(String[] args) throws InterruptedException {
    example14_63();
  }
}
