package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;

@Slf4j
public class FutureEx {

  interface SuccessCallback {
    void onSuccess(String result);
  }

  interface ExceptionCallback {
    void onError(Throwable t);
  }

  public static class CallbackFutureTask extends FutureTask<String> {
    private final SuccessCallback sc;
    private final ExceptionCallback ec;

    public CallbackFutureTask(Callable<String> callable, SuccessCallback sc, ExceptionCallback ec) {
      super(callable);
      this.sc = Objects.requireNonNull(sc);
      this.ec = Objects.requireNonNull(ec);
    }

    @Override
    protected void done() {
      try {
        sc.onSuccess(get());
      } catch (InterruptedException e) { // 예외이긴 예외지만 작업을 수행하지 말고 종료해라. 강제로 종료하지 않고 메시지를 줌.
        Thread.currentThread().interrupt(); // 예외를 던지기 보단 interrupt가 발생했다는 시그널을 주는게 중요
      } catch (ExecutionException e) { // 비동기 작업을 수행하다가 예외가 발생
        ec.onError(e.getCause()); // ExecutionException 비동기 작업에서 발생한 에러를 한번 포장했기 때문에 한번 까서 원인을 던져야 함.
      }
    }
  }

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ExecutorService es = Executors.newCachedThreadPool();

    // 비동기 결과를 가져오는 방법들..
    // 1. Future: 비동기 결과를 가져올 수 있는 방법을 알고 있는 헨들러, blocking이지만 결과를 받을 수 있음, get 메소드가 checked ex을 던지기 때문에 try~catch 블록을 만들어야 함
//    Future<String> f = es.submit(() -> {
//      Thread.sleep(2000);
//      log.info("Async");
//      return "Hello";
//    });

    // 퓨쳐 오브젝트와 콜백 메소드를 하나의 오브젝트로 만들 수 있다.
//    FutureTask<String> f = new FutureTask<>(() -> {
//      Thread.sleep(2000);
//      log.info("Async");
//      return "Hello";
//    }) {
//
//      /**
//       * 비동기 작업이 완료되면 실행
//       */
//      @Override
//      protected void done() {
//        try {
//          System.out.println(get());
//        } catch (InterruptedException e) {
//          throw new RuntimeException(e);
//        } catch (ExecutionException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    };
//
//    es.execute(f);
//
//    System.out.println(f.isDone());
//    Thread.sleep(2100);
//    System.out.println(f.isDone());
//    System.out.println(f.get()); // blocking이지만 결과를 가져오는 동안 다른 작업을 수행할 수 있기 때문에 유용한 경우도 많다.


    // 2. Callback을 만듦 (e.g. AsynchronousByteChannel)
    // 비동기를 작업하는 코드와 비지니스 로직이 담긴 코드가 섞여있다.
    CallbackFutureTask f = new CallbackFutureTask(() -> {
          Thread.sleep(2000);
          if (1 == 1) throw new RuntimeException("Async ERROR!!!");
          log.info("Async");
          return "Hello";
        },
        s -> System.out.println("Result: " + s),
        e -> System.out.println("Error: " + e.getMessage()));

    es.execute(f);
    es.shutdown();
  }
}
