package com.example.springreactive;

import java.time.Duration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class Chapter08Example {

  /**
   * 데이터 개수 제어 예제
   *  - Publisher의 데이터 emit 속도보다 Subscriber의 데이터 처리속도가 더 느릴 때 예제
   */
  private static void example8_1() {
    Flux.range(1, 5)
        .doOnRequest(data -> log.info("# doOnRequest: {}", data))
        // Reactor에서 Subscriber가 데이터 요청 개수를 직접 제어하기 위해 Subscriber 인터페이스 구현 클래스인 BaseSubscriber를 사용할 수 있다.
        .subscribe(new BaseSubscriber<>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            request(1);
          }

          @SneakyThrows
          @Override
          protected void hookOnNext(Integer value) {
            Thread.sleep(2000L);
            log.info("# hookOnNext: {}", value);
            request(1);
          }
        });
  }

  /**
   * Unbounded request 일 경우, Downstream 에 Backpressure Error 전략을 적용하는 예제
   *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, Exception을 발생 시키는 전략
   */
  private static void example8_2() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(1L)) // 0.001초에 emit
        // Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, Exception을 발생 시키는 전략
        // Downstream의 데이터처리 속도가 느려서 Upstream의 emit 속도를 따라가지 못할 경우 IllegalStateException을 발생시킨다. 이 경우 Publisher는 Error Signal을 Subscriber에게 전송하고 삭제한 데이터는 폐기한다.
        .onBackpressureError()
        .doOnNext(data -> log.info("# doOnNext: {}", data))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> {
              try {
                Thread.sleep(5L); // 0.005초마다 데이터 처리(subscriber가 더 느림)
              } catch (InterruptedException e) {}
              log.info("# onNext: {}", data);
            },
            error -> log.error("# onError", error));

    Thread.sleep(2000L);
  }

  /**
   * Unbounded request 일 경우, Downstream 에 Backpressure Drop 전략을 적용하는 예제
   *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 먼저 emit 된 데이터를 Drop 시키는 전략
   */
  private static void example8_3() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(1L))
        // Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 먼저 emit 된 데이터를 Drop 시키는 전략
        // onBackpressureDrop() Operator는 Drop된 데이터를 파라미터로 전달받을 수 있기 때문게 Drop된 데이터가 폐기되기 전에 추가 작업을 수행할 수 있다.
        .onBackpressureDrop(dropped -> log.info("# dropped: {}", dropped))
        .publishOn(Schedulers.parallel())
        .subscribe(data -> {
              try {
                Thread.sleep(5L);
              } catch (InterruptedException e) {}
              log.info("# onNext: {}", data);
            },
            error -> log.error("# onError", error));

    Thread.sleep(2000L);
  }

  /**
   * Unbounded request 일 경우, Downstream 에 Backpressure Latest 전략을 적용하는 예제
   *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
   *    버퍼 밖에서 대기하는 가장 나중에(최근에) emit 된 데이터부터 버퍼에 채우는 전략
   */
  private static void example8_4() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(1L))
        // Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 밖에서 대기하는 가장 나중에(최근에) emit 된 데이터부터 버퍼에 채우는 전략
        .onBackpressureLatest()
        .publishOn(Schedulers.parallel())
        .subscribe(data -> {
              try {
                Thread.sleep(5L);
              } catch (InterruptedException e) {}
              log.info("# onNext: {}", data);
            },
            error -> log.error("# onError", error));

    Thread.sleep(2000L);
  }

  /**
   * Unbounded request 일 경우, Downstream 에 Backpressure Buffer DROP_LATEST 전략을 적용하는 예제
   *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
   *    버퍼 안에 있는 데이터 중에서 가장 최근에(나중에) 버퍼로 들어온 데이터부터 Drop 시키는 전략
   */
  private static void example8_5() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(300L))
        .doOnNext(data -> log.info("# emitted by original Flux: {}", data))
        // Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 안에 있는 데이터 중에서 가장 최근에(나중에) 버퍼로 들어온 데이터부터 Drop 시키는 전략
        .onBackpressureBuffer(2,
            dropped -> log.info("** Overflow & Dropped: {} **", dropped),
            BufferOverflowStrategy.DROP_LATEST)
        .doOnNext(data -> log.info("[ # emitted by Buffer: {} ]", data))
        .publishOn(Schedulers.parallel(), false, 1)
        .subscribe(data -> {
              try {
                Thread.sleep(1000L);
              } catch (InterruptedException e) {}
              log.info("# onNext: {}", data);
            },
            error -> log.error("# onError", error));

    Thread.sleep(2500L);
  }

  /**
   * Unbounded request 일 경우, Downstream 에 Backpressure Buffer DROP_OLDEST 전략을 적용하는 예제
   *  - Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우,
   *    버퍼 안에 있는 데이터 중에서 가장 먼저 버퍼로 들어온 오래된 데이터부터 Drop 시키는 전략
   */
  private static void example8_6() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(300L))
        .doOnNext(data -> log.info("# emitted by original Flux: {}", data))
        // Downstream 으로 전달 할 데이터가 버퍼에 가득 찰 경우, 버퍼 안에 있는 데이터 중에서 가장 먼저 버퍼로 들어온 오래된 데이터부터 Drop 시키는 전략
        .onBackpressureBuffer(2,
            dropped -> log.info("** Overflow & Dropped: {} **", dropped),
            BufferOverflowStrategy.DROP_OLDEST)
        .doOnNext(data -> log.info("[ # emitted by Buffer: {} ]", data))
        .publishOn(Schedulers.parallel(), false, 1)
        .subscribe(data -> {
              try {
                Thread.sleep(1000L);
              } catch (InterruptedException e) {}
              log.info("# onNext: {}", data);
            },
            error -> log.error("# onError", error));

    Thread.sleep(2500L);
  }

  public static void main(String[] args) throws InterruptedException {
    example8_6();
  }
}
