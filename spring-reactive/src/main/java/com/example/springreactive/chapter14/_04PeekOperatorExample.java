package com.example.springreactive.chapter14;

import org.reactivestreams.Subscription;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * CHAPTER 14.5 Sequence의 내부 동작 확인을 위한 Operator
 */
@Slf4j
public class _04PeekOperatorExample {
  public static void main(String[] args) {
    example14_41();
  }

  private static void example14_41() {
    Flux.range(1, 5)
        .doFinally(signalType -> log.info("# doFinally 1: {}", signalType))
        .doFinally(signalType -> log.info("# doFinally 2: {}", signalType))
        .doOnNext(data -> log.info("# range > doOnNext(): {}", data))
        .doOnRequest(data -> log.info("# doOnRequest: {}", data))
        .doOnSubscribe(subscription -> log.info("# doOnSubscribe 1"))
        .doFirst(() -> log.info("# doFirst()"))
        .filter(num -> num % 2 == 1)
        .doOnNext(data -> log.info("# filter > doOnNext(): {}", data))
        .doOnComplete(() -> log.info("# doOnComplete()"))
        .subscribe(new BaseSubscriber<>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            request(1);
          }

          @Override
          protected void hookOnNext(Integer value) {
            log.info("# hookOnNext: {}", value);
            request(1);
          }
        });
  }
}