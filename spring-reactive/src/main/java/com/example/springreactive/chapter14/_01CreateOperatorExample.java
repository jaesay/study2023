package com.example.springreactive.chapter14;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

/**
 * CHAPTER 14. Sequence 생성을 위한 Operator
 */
@Slf4j
public class _01CreateOperatorExample {

  /**
   * justOrEmpty 예제
   */
  private static void example14_1() {
    Mono
        // Optional.ofNullable(null)과 같이 Optional로 감싸서 전달해도 null일 경우와 같다.
        .justOrEmpty(null)
        .subscribe(data -> {
            },
            error -> {
            },
            () -> log.info("# onComplete"));
  }

  /**
   * fromIterable 예제
   */
  private static void example14_2() {
    Flux
        .fromIterable(SampleData.coins)
        .subscribe(coin ->
            log.info("coin 명: {}, 현재가: {}", coin.getT1(), coin.getT2())
        );
  }

  /**
   * fromStream 예제
   */
  private static void example14_3() {
    Flux
        .fromStream(() -> SampleData.coinNames.stream())
        .filter(coin -> coin.equals("BTC") || coin.equals("ETH"))
        .subscribe(data -> log.info("{}", data));
  }

  /**
   * range 예제 1
   */
  private static void example14_4() {
    Flux
        .range(5, 10)
        .subscribe(data -> log.info("{}", data));
  }

  /**
   * range 예제 2
   */
  private static void example14_5() {
    Flux
        .range(7, 5)
        .map(idx -> SampleData.btcTopPricesPerYear.get(idx))
        .subscribe(tuple -> log.info("{}'s {}", tuple.getT1(), tuple.getT2()));
  }

  /**
   * defer 예제 1
   */
  private static void example14_6() throws InterruptedException {
    log.info("# start: {}", LocalDateTime.now());
    Mono<LocalDateTime> justMono = Mono.just(
        LocalDateTime.now()); // just() Operator는 Hot Publisher이기 때문에 Subscriber의 구독 여부와는 상관없이 데이터를 emit하게 된다. 그리고 구독이 발생하면 emit된 데이터를 다시 재생(replay)해서 subscribe에게 전달한다.
    Mono<LocalDateTime> deferMono = Mono.defer(() -> Mono.just(
        LocalDateTime.now())); // defer() Operator는 구독이 발생하기 전까지 데이터의 emit을 지연시키기 때문에 just() Operator를 defer()로 감싸게 되면 실제 구독이 발생해야 데이터를 emit 한다.

    Thread.sleep(2000);

    justMono.subscribe(data -> log.info("# onNext just1: {}", data));
    deferMono.subscribe(data -> log.info("# onNext defer1: {}", data));

    Thread.sleep(2000);

    justMono.subscribe(data -> log.info("# onNext just2: {}", data));
    deferMono.subscribe(data -> log.info("# onNext defer2: {}", data));
  }

  /**
   * defer 예제 2
   */
  private static void example14_7() throws InterruptedException {
    log.info("# start: {}", LocalDateTime.now());
    Mono
        .just("Hello")
        .delayElement(Duration.ofSeconds(3))
        // .switchIfEmpty(sayDefault()) // sayDefault() 호출
        .switchIfEmpty(Mono.defer(() -> sayDefault())) // sayDefault() 호출 X
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(3500);
  }

  private static Mono<String> sayDefault() {
    log.info("# Say Hi");
    return Mono.just("Hi");
  }

  /**
   * using 예제
   */
  private static void example14_8() {
    Path path = Paths.get(
        "/Users/jaeseong/workspace/study2023/spring-reactive/src/main/resources/using_example.txt");

    Flux
        .using(() -> Files.lines(path), Flux::fromStream, Stream::close)
        .subscribe(log::info);
  }

  /**
   * generate 예제 1
   */
  private static void example14_9() {
    // generate() Operator는 프로그래밍 방식으로 Signal 이벤트를 발생시키며, 특히 동기적으로 데이터를 하나씩 순차적으로 emit하고자 할 경우 사용된다.
    // generate()의 마블 다이어그램상에서는 상태 값이 숫자로 표현되지만 숫자 이외에 객체여도 상관없습니다. 다만 상태 값으로 표현되는 객체 내부에 1씩 증가하는 숫자를 포함하고 있어야 해당 숫자값을 조건으로 지정해서 onComplete Signal을 발생시킬 수 있다.
    Flux
        // 두번 째 파라미터 중 SynchronousSink는 하나의 Signal만 동기적으로 발생시킬 수 있으며 최대 하나의 상태값만 emit하는 인터페이스이다.
        .generate(() -> 0, (state, sink) -> {
          sink.next(state);
          if (state == 10) {
            sink.complete();
          }
          return ++state;
        })
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * generate 예제 2
   */
  private static void example14_10() {
    final int dan = 3;
    Flux
        .generate(() -> Tuples.of(dan, 1), (state, sink) -> {
          sink.next(state.getT1() + " * " +
              state.getT2() + " = " + state.getT1() * state.getT2());
          if (state.getT2() == 9) {
            sink.complete();
          }
          return Tuples.of(state.getT1(), state.getT2() + 1);
        }, state -> log.info("# 구구단 {}단 종료!", state.getT1()))
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * generate 예제 3
   */
  private static void example14_11() {
    Map<Integer, Tuple2<Integer, Long>> map = SampleData.getBtcTopPricesPerYearMap();

    Flux
        .generate(() -> 2019, (state, sink) -> {
          if (state > 2021) {
            sink.complete();
          } else {
            sink.next(map.get(state));
          }

          return ++state;
        })
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  static int SIZE = 0;
  static int COUNT = -1;
  final static List<Integer> DATA_SOURCE = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

  /**
   * create 예제 1 - pull 방식
   */
  private static void example14_12() {
    log.info("# start");
    // create() Operator는 generate() Operator처럼 프로그매이 방식으로 Signal 이벤트를 발생시키지만 generate() Operator와 몇 가지 차이점이 있다. generate() Operator는 데이터를 동기적으로 한 번에 한 건씩 emit할 수 있는 반면에, create() Operator는 한번에 여러건의 데이터를 비동기적으로 emit할 수 있다.
    Flux.create((FluxSink<Integer> sink) -> {
      // Subscriber 쪽에서 create() 메서드를 호출하면 create() Operator 내부에서 sink.onRequest() 메서드의 람다 표현식이 실행된다.
      sink.onRequest(n -> {
        try {
          Thread.sleep(1000L);
          for (int i = 0; i < n; i++) {
            if (COUNT >= 9) {
              sink.complete();
            } else {
              COUNT++;
              sink.next(DATA_SOURCE.get(COUNT));
            }
          }
        } catch (InterruptedException e) {
        }
      });

      // FluxSink의 onDispose() 메서드에서 dispose의 의미는 FluxSink 관점에서 FluxSink가 더이상 사용되지 않는다는 의미이다. 구독을 취소하기 위해 사용되는 Disposable.dispose() 메서드의 dispose와 의미는 같지만 dispose 하려는 대상이 다르다.
      sink.onDispose(() -> log.info("# clean up"));
    }).subscribe(new BaseSubscriber<>() {
      @Override
      protected void hookOnSubscribe(Subscription subscription) {
        // Subscriber가 request() 메서드를 통해 요청을 보내면 Publisher가 해당 요청 개수만큼의 데이터를 emit하는 일종의 pull 방식으로 데이터를 처리한다.
        request(2);
      }

      @Override
      protected void hookOnNext(Integer value) {
        SIZE++;
        log.info("# onNext: {}", value);
        if (SIZE == 2) {
          request(2);
          SIZE = 0;
        }
      }

      @Override
      protected void hookOnComplete() {
        log.info("# onComplete");
      }
    });
  }

  /**
   * create 예제 2 - push 방식
   */
  private static void example14_13() throws InterruptedException {
    CryptoCurrencyPriceEmitter priceEmitter = new CryptoCurrencyPriceEmitter();

    Flux.create((FluxSink<Integer> sink) ->
            priceEmitter.setListener(new CryptoCurrencyPriceListener() {
              @Override
              public void onPrice(List<Integer> priceList) {
                priceList.forEach(price -> {
                  sink.next(price);
                });
              }

              @Override
              public void onComplete() {
                sink.complete();
              }
            }))
        .publishOn(Schedulers.parallel())
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> {
            },
            () -> log.info("# onComplete"));

    Thread.sleep(3000L);

    priceEmitter.flowInto();

    Thread.sleep(2000L);
    priceEmitter.complete();
  }

  static int start = 1;
  static int end = 4;

  /**
   * create 예제
   *  - Backpressure 전략 적용
   */
  private static void example14_14() throws InterruptedException {
    // create() Operator는 한 번에 여러 건의 데이터를 비동기적으로 emit할 수 있기 때문에 Backpressure 전략이 필요하다.
    Flux.create((FluxSink<Integer> emitter) -> {
          emitter.onRequest(n -> {
            log.info("# requested: " + n);
            try {
              Thread.sleep(500L);
              // 2개 요청했지만 4개 데이터 emit 하여 2개는 drop된다.
              for (int i = start; i <= end; i++) {
                emitter.next(i);
              }
              start += 4;
              end += 4;
            } catch (InterruptedException e) {}
          });

          emitter.onDispose(() -> {
            log.info("# clean up");
          });
        }, FluxSink.OverflowStrategy.DROP)
        .subscribeOn(Schedulers.boundedElastic())
        .publishOn(Schedulers.parallel(), 2)
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(3000L);
  }

  public static void main(String[] args) throws InterruptedException {
    example14_14();
  }
}
