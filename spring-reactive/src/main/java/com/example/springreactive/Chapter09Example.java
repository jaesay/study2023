package com.example.springreactive;

import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Chapter09Example {

  /**
   * create() Operator를 사용하는 예제
   *  - 일반적으로 Publisher가 단일 쓰레드에서 데이터 생성한다.
   */
  private static void example9_1() throws InterruptedException {
    int tasks = 6;
    Flux
        // Reactor에서 프로그래밍 방식으로 Signal을 전송하는 가장 일반적인 방법은 generate() Operator나 create() Operator 등을 사용하는 것인데, 이는 Reactor에서 Sinks를 지원하기 전부터 이미 사용하던 방식이다. 일반적으로 Publisher가 단일 쓰레드에서 데이터 생성한다.
        .create((FluxSink<String> sink) -> {
          IntStream
              .range(1, tasks)
              .forEach(n -> sink.next(doTask(n)));
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(n -> log.info("# create(): {}", n))
        .publishOn(Schedulers.parallel())
        .map(result -> result + " success!")
        .doOnNext(n -> log.info("# map(): {}", n))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(500L);
  }

  private static String doTask(int taskNumber) {
    return "task " + taskNumber + " result";
  }

  /**
   * Sinks를 사용하는 예제
   *  - Publisher의 데이터 생성을 멀티 쓰레드에서 진행해도 Thread safe 하다.
   */
  private static void example9_2() throws InterruptedException {
    int tasks = 6;

    // Sinks는 프로그래밍 방식으로 Signal을 전송할 수 있으며, Publisher의 데이터 생성을 멀티 쓰레드에서 진행해도 Thread safe 하다.
    Sinks.Many<String> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
    Flux<String> fluxView = unicastSink.asFlux();
    IntStream
        .range(1, tasks)
        .forEach(n -> {
          try {
            new Thread(() -> {
              unicastSink.emitNext(doTask(n), FAIL_FAST);
              log.info("# emitted: {}", n);
            }).start();
            Thread.sleep(100L);
          } catch (InterruptedException e) {
            log.error(e.getMessage());
          }
        });

    fluxView
        .publishOn(Schedulers.parallel())
        .map(result -> result + " success!")
        .doOnNext(n -> log.info("# map(): {}", n))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(200L);
  }

  /**
   * Sinks.One 예제
   *  - emit 된 데이터 중에서 단 하나의 데이터만 Subscriber에게 전달한다. 나머지 데이터는 Drop 됨.
   */
  private static void example9_4() {
    Sinks.One<String> sinkOne = Sinks.one();
    Mono<String> mono = sinkOne.asMono();

    // emit 된 데이터 중에서 단 하나의 데이터만 Subscriber에게 전달한다. 나머지 데이터는 Drop 된다.
    sinkOne.emitValue("Hello Reactor", FAIL_FAST);
    sinkOne.emitValue("Hi Reactor", FAIL_FAST);
    sinkOne.emitValue(null, FAIL_FAST);

    mono.subscribe(data -> log.info("# Subscriber1 {}", data));
    mono.subscribe(data -> log.info("# Subscriber2 {}", data));
  }

  /**
   * Sinks.Many 예제
   *  - unicast()통해 단 하나의 Subscriber만 데이터를 전달 받을 수 있다
   */
  private static void example9_8() {
    // unicast()통해 단 하나의 Subscriber만 데이터를 전달 받을 수 있다. 단 하나의 Subscriber에게만 데이터를 emit할 수 있다.
    Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
    Flux<Integer> fluxView = unicastSink.asFlux();

    unicastSink.emitNext(1, FAIL_FAST);
    unicastSink.emitNext(2, FAIL_FAST);


    fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));

    unicastSink.emitNext(3, FAIL_FAST);

    fluxView.subscribe(data -> log.info("# Subscriber2: {}", data));
  }

  /**
   * Sinks.Many 예제
   *  - multicast()를 사용해서 하나 이상의 Subscriber에게 데이터를 emit하는 예제
   */
  private static void example9_9() {
    // 하나 이상의 Subscriber에게 데이터를 emit할 수 있다. Warm up의 특징을 가지는 Hot Sequence로 동작한다.
    Sinks.Many<Integer> multicastSink =
        Sinks.many().multicast().onBackpressureBuffer();
    Flux<Integer> fluxView = multicastSink.asFlux();

    multicastSink.emitNext(1, FAIL_FAST);
    multicastSink.emitNext(2, FAIL_FAST);

    fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));
    fluxView.subscribe(data -> log.info("# Subscriber2: {}", data));

    multicastSink.emitNext(3, FAIL_FAST);
  }

  /**
   * Sinks.Many 예제
   *  - replay()를 사용하여 이미 emit된 데이터 중에서 특정 개수의 최신 데이터만 전달하는 예제
   */
  private static void example9_10() {
    // replay()와 limit()을 사용하여 이미 emit된 데이터 중에서 특정 개수의 최신 데이터만 전달한다.
    Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(2);
    Flux<Integer> fluxView = replaySink.asFlux();

    replaySink.emitNext(1, FAIL_FAST);
    replaySink.emitNext(2, FAIL_FAST);
    replaySink.emitNext(3, FAIL_FAST);

    fluxView.subscribe(data -> log.info("# Subscriber1: {}", data));

    replaySink.emitNext(4, FAIL_FAST);

    fluxView.subscribe(data -> log.info("# Subscriber2: {}", data));
  }

  public static void main(String[] args) throws InterruptedException {
    example9_10();
  }
}
