package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SchedulerEx {

  public static void main(String[] args) {
    Publisher<Integer> pub = sub ->
        sub.onSubscribe(new Subscription() {
          @Override
          public void request(long n) {
            log.debug("request");
            sub.onNext(1);
            sub.onNext(2);
            sub.onNext(3);
            sub.onNext(4);
            sub.onNext(5);
            sub.onComplete();
          }

          @Override
          public void cancel() {
          }
        });

    // subscribeOn: pub은 느리고 sub은 빠를 경우
    Publisher<Integer> subOnPub = sub -> {
      ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
        @Override
        public String getThreadNamePrefix() {
          return "subOn-";
        }
      });
      es.execute(() -> pub.subscribe(sub));
    };

    // publishOn: pub은 빠르고 sub은 느릴 경우, 데이터 소비하는 부분을 별개의 쓰레드에서 동작하도록 함
    Publisher<Integer> pubOnPub = sub ->

        // 다양한 이유로 두가지 전략을 같이 쓰기도 한다: latency, 실행속도, 나중에 동작, 쓰레드를 빠르게 반환 등등..
        pub.subscribe(new Subscriber<>() {

          // 단일 쓰레드에서 동작하기 때문에 동시에 또는 순서가 바뀌어 실행되지 않게 된다.
          ExecutorService es = Executors.newSingleThreadExecutor(new CustomizableThreadFactory() {
            @Override
            public String getThreadNamePrefix() {
              return "pubOn-";
            }
          });

          @Override
          public void onSubscribe(Subscription s) {
            sub.onSubscribe(s);
          }

          @Override
          public void onNext(Integer integer) {
            es.execute(() -> sub.onNext(integer));
          }

          @Override
          public void onError(Throwable t) {
            es.execute(() -> sub.onError(t));
            es.shutdown();
          }

          @Override
          public void onComplete() {
            es.execute(() -> sub.onComplete());
            es.shutdown();
          }
        });

    pubOnPub.subscribe(new Subscriber<>() {
      @Override
      public void onSubscribe(Subscription s) {
        log.debug("onSubscribe");
        s.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(Integer i) {
        log.debug("onNext: {}", i);
      }

      @Override
      public void onError(Throwable t) {
        log.debug("onError: {}", t.getMessage());
      }

      @Override
      public void onComplete() {
        log.debug("onComplete");
      }
    });

    log.debug("exit");
  }
}
