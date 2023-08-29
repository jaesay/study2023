package com.example.springreactive;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * CHAPTER 10. Scheduler
 */
@Slf4j
public class Chapter10Example {

  /**
   * subscribeOn() 기본 예제
   *  - 구독 시점에 Publisher의 실행을 위한 쓰레드를 지정한다
   */
  private static void example10_1() throws InterruptedException {
    Flux.fromArray(new Integer[] {1, 3, 5, 7})
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(data -> log.info("# doOnNext: {}", data))
        .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  /**
   * publishOn() 기본 예제
   *  - Operator 체인에서 Downstream Operator의 실행을 위한 쓰레드를 지정한다.
   */
  private static void example10_2() throws InterruptedException {
    Flux.fromArray(new Integer[] {1, 3, 5, 7})
        .doOnNext(data -> log.info("# doOnNext: {}", data))
        .doOnSubscribe(subscription -> log.info("# doOnSubscribe"))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  /**
   * parallel() 기본 사용 예제
   * - parallel()만 사용할 경우에는 병렬로 작업을 수행하지 않는다.
   * - runOn()을 사용해서 Scheduler를 할당해주어야 병렬로 작업을 수행한다.
   * - **** CPU 코어 갯수내에서 worker thread를 할당한다. ****
   */
  private static void example10_3() throws InterruptedException {
    Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19})
        .parallel()
        .runOn(Schedulers.parallel())
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(100L);
  }

  /**
   * parallel() 기본 사용 예제
   * - parallel()만 사용할 경우에는 병렬로 작업을 수행하지 않는다.
   * - runOn()을 사용해서 Scheduler를 할당해주어야 병렬로 작업을 수행한다.
   * - **** CPU 코어 갯수내에서 worker thread를 할당한다. ****
   */
  private static void example10_4() throws InterruptedException {
    Flux.fromArray(new Integer[]{1, 3, 5, 7, 9, 11, 13, 15, 17, 19})
        .parallel(4)
        .runOn(Schedulers.parallel())
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(100L);
  }

  /**
   * subscribeOn()과 publishOn()의 동작 과정 예
   *  - subscribeOn()과 publishOn()을 사용하지 않은 경우
   *      - Sequence의 Operator 체인에서 최초의 쓰레드는 subscribe()가
   *        호출되는 scope에 있는 쓰레드이다.
   */
  private static void example10_5() throws InterruptedException {
    Flux
        .fromArray(new Integer[] {1, 3, 5, 7})
        .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# doOnNext filter: {}", data))
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# doOnNext map: {}", data))
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * subscribeOn()과 publishOn()의 동작 과정 예
   *  - 하나의 publishOn()만 사용한 경우
   *      - publishOn() 아래 쪽 Operator들의 실행 쓰레드를 변경한다.
   *
   */
  private static void example10_6() throws InterruptedException {
    Flux
        .fromArray(new Integer[] {1, 3, 5, 7})
        .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
        .publishOn(Schedulers.parallel())
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# doOnNext filter: {}", data))
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# doOnNext map: {}", data))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  /**
   * subscribeOn()과 publishOn()의 동작 과정 예
   *  - 두 개의 publishOn()을 사용한 경우
   *      - 다음 publishOn()을 만나기 전까지 publishOn() 아래 쪽 Operator들의 실행 쓰레드를 변경한다.
   *
   */
  private static void example10_7() throws InterruptedException {
    Flux
        .fromArray(new Integer[] {1, 3, 5, 7})
        .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
        .publishOn(Schedulers.parallel())
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# doOnNext filter: {}", data))
        .publishOn(Schedulers.parallel())
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# doOnNext map: {}", data))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  /**
   * subscribeOn()과 publishOn()의 동작 과정 예
   *  - subscribeOn()과 publishOn()을 함께 사용한 경우
   *      - subscribeOn()은 구독 직후에 실행될 쓰레드를 지정하고, publishOn()을 만나기 전까지 쓰레드를 변경하지 않는다.
   *
   */
  private static void example10_8() throws InterruptedException {
    Flux
        .fromArray(new Integer[] {1, 3, 5, 7})
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(data -> log.info("# doOnNext fromArray: {}", data))
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# doOnNext filter: {}", data))
        .publishOn(Schedulers.parallel())
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# doOnNext map: {}", data))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  /**
   * Schedulers.immediate() 예
   *  - 별도의 쓰레드를 할당하지 않고, 현재 쓰레드에서 실행된다.
   *
   */
  private static void example10_9() throws InterruptedException {
    Flux
        .fromArray(new Integer[] {1, 3, 5, 7})
        .publishOn(Schedulers.parallel())
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# doOnNext filter: {}", data))
        .publishOn(Schedulers.immediate())
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# doOnNext map: {}", data))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(200L);
  }

  /**
   * Schedulers.single() 예
   *  - Scheduler가 제거될 때까지 동일한 쓰레드를 재사용한다.
   *
   */
  private static void example10_10() throws InterruptedException {
    doTask10_10("task1")
        .subscribe(data -> log.info("# onNext: {}", data));

    doTask10_10("task2")
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(200L);
  }

  private static Flux<Integer> doTask10_10(String taskName) {
    return Flux.fromArray(new Integer[] {1, 3, 5, 7})
        .publishOn(Schedulers.single())
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# {} doOnNext filter: {}", taskName, data))
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# {} doOnNext map: {}", taskName, data));
  }

  /**
   * Schedulers.newSingle() 예
   *  - 호출할 때 마다 매번 하나의 쓰레드를 새로 생성한다.
   *
   */
  private static void example10_11() throws InterruptedException {
    doTask10_11("task1")
        .subscribe(data -> log.info("# onNext: {}", data));

    doTask10_11("task2")
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(200L);
  }

  private static Flux<Integer> doTask10_11(String taskName) {
    return Flux.fromArray(new Integer[] {1, 3, 5, 7})
        .publishOn(Schedulers.newSingle("new-single", true))
        .filter(data -> data > 3)
        .doOnNext(data -> log.info("# {} doOnNext filter: {}", taskName, data))
        .map(data -> data * 10)
        .doOnNext(data -> log.info("# {} doOnNext map: {}", taskName, data));
  }

  public static void main(String[] args) throws InterruptedException {
    example10_11();
  }

}
