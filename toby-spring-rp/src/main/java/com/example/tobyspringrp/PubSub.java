package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Operator: 중간에서 데이터를 가공
 * -> downstream
 * <- upstream
 * iterPub -> [T] -> mapPub(Operator) -> [R] -> logSub
 *                <- subscribe(logSub)
 *                -> onSubscribe(s)
 *                -> onNext
 *                -> onNext
 *                -> onComplete
 */
@Slf4j
public class PubSub {

  public static void main(String[] args) {
    Publisher<Integer> pub = iterPub(Stream.iterate(1, i -> i + 1).limit(10).collect(toList()));
//    Publisher<String> mapPub = mapPub(pub, i -> "[" + i + "]");
//    Publisher<Integer> map2Pub = mapPub(mapPub, i-> -i);
    Publisher<StringBuilder> reducePub = reducePub(pub, new StringBuilder(), (a, b) -> a.append(b + ","));
    // 1. publisher 구독
    reducePub.subscribe(logSub());
  }

  /**
   * Operator
   */
  private static <T, R> Publisher<R> reducePub(Publisher<T> pub, R init, BiFunction<R, T, R> bf) {
    return new Publisher<>() {
      @Override
      public void subscribe(Subscriber<? super R> sub) {
        // 중개하는 Subscriber를 통해 가공
        pub.subscribe(new DelegateSub<T, R>(sub) {
          R result = init;

          @Override
          public void onNext(T i) {
            result = bf.apply(this.result, i);
          }

          @Override
          public void onComplete() {
            sub.onNext(result);
            sub.onComplete();
          }
        });
      }
    };
  }

  private static <T, R> Publisher<R> mapPub(Publisher<T> pub, Function<T, R> f) {
    return sub -> pub.subscribe(new DelegateSub<T, R>(sub) {
      @Override
      public void onNext(T i) {
        sub.onNext(f.apply(i));
      }
    });
  }

  private static <T> Subscriber<T> logSub() {

    return new Subscriber<>() {
      /**
       * 2. subscription을 사용하여 얼마만큼 받고 싶다(의도)라는 요청
       */
      @Override
      public void onSubscribe(Subscription s) {
        log.debug("onSubscribe");
        s.request(Long.MAX_VALUE); // onSubscribe에서는 subscribe를 한 동일한 스레드(e.g. main)에서 요청해야 한다.
      }

      /**
       * 3. 데이터 전달
       */
      @Override
      public void onNext(T i) {
        log.debug("onNext: {}", i);
      }

      @Override
      public void onError(Throwable t) {
        log.debug("onError: {}", t.getMessage());
      }

      /**
       * 4. 완료
       */
      @Override
      public void onComplete() {
        log.debug("onComplete");
      }
    };
  }

  private static Publisher<Integer> iterPub(List<Integer> iter) {
    return sub -> {
      // 1. sub 의 onSubscribe 호출,
      // Subscription은 구독이 일어나면 발생하는 액션을 정의
      sub.onSubscribe(new Subscription() {

        /**
         * Subscriber 가 구독을 할 때마다 한번 씩 일어난다.
         */
        @Override
        public void request(long n) {
          try {
            iter.forEach(i -> sub.onNext(i));
            sub.onComplete();

          } catch (Throwable t) {
            sub.onError(t);
          }
        }

        /**
         * pub에게 더 이상 데이터를 보내지 말라고 요청할 때 사용한다.
         */
        @Override
        public void cancel() {

        }
      });
    };
  }
}
