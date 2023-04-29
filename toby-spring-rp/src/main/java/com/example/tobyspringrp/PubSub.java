package com.example.tobyspringrp;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.TimeUnit;

public class PubSub {

  public static void main(String[] args) throws InterruptedException {
    Iterable<Integer> itr = List.of(1, 2, 3, 4, 5);
    ExecutorService es = Executors.newSingleThreadExecutor();

    Publisher<Integer> publisher = new Publisher<>() {
      @Override
      public void subscribe(Subscriber<? super Integer> subscriber) {
        subscriber.onSubscribe(new Subscription() {
          Iterator<Integer> it = itr.iterator();

          @Override
          public void request(long n) {
            es.execute(() -> {
              int i = 0;
              try {
                while (i++ < n) {
                  if (this.it.hasNext()) {
                    subscriber.onNext(it.next()); // 2. 다음번 호출
                  } else {
                    subscriber.onComplete(); // 마지막. 다보냈으면 완료
                    break;
                  }
                }
              } catch (RuntimeException e) {
                subscriber.onError(e); // 에러 발생 시 에러 처리
              }
            });
          }

          @Override
          public void cancel() {

          }
        });
      }
    };

    Subscriber<Integer> subscriber = new Subscriber<>() {
      Subscription subscription;

      @Override
      public void onSubscribe(Subscription subscription) {
        System.out.println(Thread.currentThread().getName() + " onSubscribe"); // main onSubscribe
        this.subscription = subscription;
        // 1. subscription을 사용하여 얼마만큼 받고 싶다(의도)라는 요청
        this.subscription.request(1); // onSubscribe에서는 subscribe를 한 동일한 스레드(e.g. main)에서 요청해야 한다.
      }

      @Override
      public void onNext(Integer item) {
        System.out.println(Thread.currentThread().getName() + " onNext: " + item); // pool-1-thread-1 onNext: N
        this.subscription.request(1);
      }

      @Override
      public void onError(Throwable throwable) {
        System.out.println(Thread.currentThread().getName() + " onError: " + throwable.getMessage());
      }

      @Override
      public void onComplete() {
        System.out.println(Thread.currentThread().getName() + " onComplete"); // pool-1-thread-1 onComplete
      }
    };

    publisher.subscribe(subscriber);
    es.awaitTermination(10, TimeUnit.SECONDS);
    es.shutdown();
  }
}
