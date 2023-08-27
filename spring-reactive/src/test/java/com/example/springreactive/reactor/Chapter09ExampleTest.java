package com.example.springreactive.reactor;

import static com.example.springreactive.reactor.TestUtils.print;
import static reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST;

import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

public class Chapter09ExampleTest {

  @Test
  @DisplayName("create() Operator를 사용하는 예제")
  void example9_1() throws InterruptedException {
    int tasks = 6;
    Flux
        // Reactor에서 프로그래밍 방식으로 Signal을 전송하는 가장 일반적인 방법은 generate() Operator나 create() Operator 등을 사용하는 것인데, 이는 Reactor에서 Sinks를 지원하기 전부터 이미 사용하던 방식이다. 일반적으로 Publisher가 단일 쓰레드에서 데이터 생성한다.
        .create((FluxSink<String> sink) -> {
          IntStream
              .range(1, tasks)
              .forEach(n -> sink.next(doTask(n)));
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(n -> print("# create(): %s", n))
        .publishOn(Schedulers.parallel())
        .map(result -> result + " success!")
        .doOnNext(n -> print("# map(): %s", n))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> print("# onNext: %s", data));

    Thread.sleep(500L);
  }

  private static String doTask(int taskNumber) {
    return "task " + taskNumber + " result";
  }

  @Test
  @DisplayName("Sinks를 사용하는 예제")
  void example9_2() throws InterruptedException {
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
              print("# emitted: %s", n);
            }).start();
            Thread.sleep(100L);
          } catch (InterruptedException e) {
            print(e.getMessage());
          }
        });

    fluxView
        .publishOn(Schedulers.parallel())
        .map(result -> result + " success!")
        .doOnNext(n -> print("# map(): %s", n))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> print("# onNext: %s", data));

    Thread.sleep(200L);
  }

  @Test
  @DisplayName("Sinks.One 예제")
  void example9_4() {
    Sinks.One<String> sinkOne = Sinks.one();
    Mono<String> mono = sinkOne.asMono();

    // emit 된 데이터 중에서 단 하나의 데이터만 Subscriber에게 전달한다. 나머지 데이터는 Drop 된다.
    sinkOne.emitValue("Hello Reactor", FAIL_FAST);
    sinkOne.emitValue("Hi Reactor", FAIL_FAST);
    sinkOne.emitValue(null, FAIL_FAST);

    mono.subscribe(data -> print("# Subscriber1 %s", data));
    mono.subscribe(data -> print("# Subscriber2 %s", data));
  }

  @Test
  @DisplayName("Sinks.Many unicast 예제")
  void example9_8() {
    // unicast()통해 단 하나의 Subscriber만 데이터를 전달 받을 수 있다. 단 하나의 Subscriber에게만 데이터를 emit할 수 있다.
    Sinks.Many<Integer> unicastSink = Sinks.many().unicast().onBackpressureBuffer();
    Flux<Integer> fluxView = unicastSink.asFlux();

    unicastSink.emitNext(1, FAIL_FAST);
    unicastSink.emitNext(2, FAIL_FAST);

    fluxView.subscribe(data -> print("# Subscriber1: %s", data));

    unicastSink.emitNext(3, FAIL_FAST);

    fluxView.subscribe(data -> print("# Subscriber2: %s", data));
  }

  @Test
  @DisplayName("Sinks.Many multicast 예제")
  void example9_9() {
    // 하나 이상의 Subscriber에게 데이터를 emit할 수 있다. Warm up의 특징을 가지는 Hot Sequence로 동작한다.
    Sinks.Many<Integer> multicastSink = Sinks.many().multicast().onBackpressureBuffer();
    Flux<Integer> fluxView = multicastSink.asFlux();

    multicastSink.emitNext(1, FAIL_FAST);
    multicastSink.emitNext(2, FAIL_FAST);

    fluxView.subscribe(data -> print("# Subscriber1: %s", data));
    fluxView.subscribe(data -> print("# Subscriber2: %s", data));

    multicastSink.emitNext(3, FAIL_FAST);
  }

  @Test
  @DisplayName("Sinks.Many replay 예제")
  void example9_10() {
    // replay()와 limit()을 사용하여 이미 emit된 데이터 중에서 특정 개수의 최신 데이터만 전달한다.
    Sinks.Many<Integer> replaySink = Sinks.many().replay().limit(2);
    Flux<Integer> fluxView = replaySink.asFlux();

    replaySink.emitNext(1, FAIL_FAST);
    replaySink.emitNext(2, FAIL_FAST);
    replaySink.emitNext(3, FAIL_FAST);

    fluxView.subscribe(data -> print("# Subscriber1: %s", data));

    replaySink.emitNext(4, FAIL_FAST);

    fluxView.subscribe(data -> print("# Subscriber2: %s", data));
  }

}
