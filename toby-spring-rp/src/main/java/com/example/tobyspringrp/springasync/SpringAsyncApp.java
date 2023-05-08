package com.example.tobyspringrp.springasync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

@EnableAsync
@Slf4j
@SpringBootApplication
public class SpringAsyncApp {

  @RestController
  public static class MyController {

    /**
     * HTTP Streaming
     * HTTP SSE(Server-Sent Events) 표준에 따라서 데이터를 스트리밍 방식으로 응답하면 데이터를 나눠서 보낼 수 있다.
     */
//    @GetMapping("/emitter")
//    public ResponseBodyEmitter emitter() {
//      ResponseBodyEmitter emitter = new ResponseBodyEmitter();
//
//      Executors.newSingleThreadExecutor().submit(() -> {
//        for (int i = 0; i < 50; i++) {
//          try {
//            emitter.send("<p>Stream " + i + "</p>");
//            Thread.sleep(100);
//          } catch (Exception e) {
//            throw new RuntimeException(e);
//          }
//        }
//      });
//
//      return emitter;
//    }

    Queue<DeferredResult<String>> results = new ConcurrentLinkedQueue<>();

    /**
     * 요청을 받으면 어떤 작업을 수행하지 않고 대기하다가..외부에서 어떤 이벤트가 발생을 하면 이벤트에 의해 작업을 수행한 후에 결과를 응답
     * 각각에 요청을 DeferredResult 에 담아놓고 있다가 한꺼번에 결과를 또는 하나하나씩 줄 수 있다.
     * 외부의 이벤트에 의해서 기존에 지연되어 있는 HTTP 응답을 나중에 써줄 수 있게 한다.
     * 추가적인 워커 쓰레드 없이 처리할 수 있다.
     * DeferredResult 를 non-blocking io 함께 잘활용하면 서블릿 쓰레드를 최소한으로 사용하면서 많은 요청을 처리할 수 있다.
     */
    @GetMapping("/dr")
    public DeferredResult<String> dr() {
      log.info("dr");
      DeferredResult<String> dr = new DeferredResult<>(600_000L);
      results.add(dr);
      return dr; // 대기하고 있다가 setResult(/dr/event) 가 호출되면 응답
    }

    @GetMapping("/dr/count")
    public String drcount() {
      return String.valueOf(results.size());
    }

    @GetMapping("dr/event")
    public String drevent(String msg) {
      results.forEach(dr -> {
        dr.setResult("Hello " + msg);
        results.remove();
      });
      return "OK";
    }

    /**
     * 비동기 작업을 수행하고 결과를 MVC 메소드 결과처럼 처리해준다.
     * 추가적인 워커 쓰레드들이 필요하긴 하지만 서블릿 쓰레드가 아닌 워커 쓰레드가 비동기 작업을 처리할 수 있다.
     */
    @GetMapping("/callable")
    public Callable<String> callable() throws InterruptedException {
      log.info("callable");
      // 서블릿 쓰레드는 반환 후 작업 쓰레드에서 수행, 이후 작업이 완료되면 새로운 서블릿 쓰레드를 가지고 리턴
      return () -> {
        log.info("async");
        Thread.sleep(2000);
        return "Hello";
      };
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringAsyncApp.class, args);
  }


//  @Component
//  public static class  MyService {
//
//    /**
//     *
//     * @return Future, ListenableFuture: 스프링 4.0부터 제공하는 callback을 활용할 수 있는 Future, CompletableFuture
//     * @throws InterruptedException
//     */
//    @Async("tp")
//    public ListenableFuture<String> hello() throws InterruptedException {
//      log.info("hello()");
//      Thread.sleep(2000);
//      return new AsyncResult<>("Hello");
//    }
//  }
//
//  @Bean
//  ThreadPoolTaskExecutor tp() {
//    ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
//    te.setCorePoolSize(10); // 1. 기본 10개
//    te.setMaxPoolSize(100); // 3. 큐사이즈가 다 찼을 시 100개를 추가로 생성
//    te.setQueueCapacity(200); // 2. 가용할 수 있는 쓰레드가 없을 때 200개까지 대기
////    te.setAllowCoreThreadTimeOut(); // 코어 쓰레드 타임아웃 설정
////    te.getKeepAliveSeconds() // setMaxPoolSize 로 생성된 쓰레드가 반환되고 해당 시간동안 반환이 안되면 삭제
////    te.setTaskDecorator(); // 쓰레드를 새로 만들거나 반환하는 시점에 callback을 실행할 수 있음
//    te.setThreadNamePrefix("mythread-");
//    te.initialize();
//    return te;
//  }
//
//
//  @Autowired
//  MyService myService;
//
//  @Bean
//  ApplicationRunner run() {
//    return args -> {
//      log.info("run()");
//      ListenableFuture<String> f = myService.hello();
//      f.addCallback(s -> System.out.println(s), e -> System.out.println(e.getMessage()));
//      log.info("exit");
//    };
//  }
}
