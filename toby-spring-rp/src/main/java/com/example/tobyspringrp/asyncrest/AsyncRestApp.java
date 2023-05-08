package com.example.tobyspringrp.asyncrest;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
@EnableAsync
@Slf4j
public class AsyncRestApp {

  @RestController
  public static class MyController {

    AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

    @Autowired
    MyService myService;

    static final String URL1 = "http://localhost:8081/service?req={req}";
    static final String URL2 = "http://localhost:8081/service2?req={req}";

    @GetMapping("/rest")
    public DeferredResult<String> rest(int idx) {
      DeferredResult<String> dr = new DeferredResult<>();

      toCF(asyncRestTemplate.getForEntity(URL1, String.class, "hello" + idx))
          .thenCompose(s -> {
            log.info("thenCompose");
//            if (1 == 1) throw new RuntimeException("ERROR");
            return toCF(asyncRestTemplate.getForEntity(URL2, String.class, s.getBody()));
          })
//          .thenApply(s2 -> myService.work(s2.getBody()))
          .thenApplyAsync(s2 -> {
            log.info("thenApplyAsync");
            return myService.work(s2.getBody());
          })
          .thenAccept(s3 -> {
            log.info("thenAccept");
            dr.setResult(s3);
          })
          .exceptionally(e -> {
            dr.setErrorResult(e.getMessage());
            return null;
          });

//      Completion.from(asyncRestTemplate.getForEntity(URL1, String.class, "hello" + idx))
//          .andApply(s -> asyncRestTemplate.getForEntity(URL2, String.class, s.getBody()))
//          .andApply(s -> myService.work(s.getBody()))
//          .andError(e -> dr.setErrorResult(e.toString()))
//          .andAccept(s -> dr.setResult(s));

//      ListenableFuture<ResponseEntity<String>> f1 = asyncRestTemplate.getForEntity(
//          "http://localhost:8081/service?req={req}", String.class, "hello" + idx);
//
//      // 콜백 헬 + 에러처리 중복 코드
//      f1.addCallback(s -> {
//        ListenableFuture<ResponseEntity<String>> f2 = asyncRestTemplate.getForEntity(
//            "http://localhost:8081/service2?req={req}", String.class, s.getBody());
//        f2.addCallback(s2 -> {
//          ListenableFuture<String> f3 = myService.work(s2.getBody());
//          f3.addCallback(s3 -> {
//            dr.setResult(s3);
//          }, e -> {
//            dr.setErrorResult(e.getMessage());
//          });
//        }, e -> {
//          dr.setErrorResult(e.getMessage());
//        });
//      }, e -> {
//        dr.setErrorResult(e.getMessage());
//      });

      return dr;
    }

    <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
      CompletableFuture<T> cf = new CompletableFuture<>();
      lf.addCallback(s -> cf.complete(s), e -> cf.completeExceptionally(e));
      return cf;
    }
  }

  public static class AcceptCompletion<S> extends Completion<S, Void> {
    private final Consumer<S> con;
    public AcceptCompletion(Consumer<S> con) {
      this.con = con;
    }

    @Override
    void run(S value) {
      con.accept(value);
    }
  }

  public static class ErrorCompletion<T> extends Completion<T, T> {
    private final Consumer<Throwable> econ;
    public ErrorCompletion(Consumer<Throwable> econ) {
      this.econ = econ;
    }

    @Override
    void run(T value) {
      if (next != null) next.run(value);
    }

    @Override
    void error(Throwable e) {
      econ.accept(e);
    }
  }

  public static class ApplyCompletion<S, T> extends Completion<S, T> {
    private final Function<S, ListenableFuture<T>> fn;
    public ApplyCompletion(Function<S, ListenableFuture<T>> fn) {
      this.fn = fn;
    }

    @Override
    void run(S value) {
      ListenableFuture<T> lf = fn.apply(value);
      lf.addCallback(s -> complete(s), e -> error(e));
    }
  }

  public static class Completion<S, T> {

    Completion next;

    public void andAccept(Consumer<T> con) {
      Completion<T, Void> c = new AcceptCompletion<>(con);
      this.next = c;
    }

    public Completion<T, T> andError(Consumer<Throwable> econ) {
      Completion<T, T> c = new ErrorCompletion<>(econ);
      this.next = c;
      return c;
    }

    public <V> Completion<T, V> andApply(Function<T, ListenableFuture<V>> fn) {
      Completion<T, V> c = new ApplyCompletion<>(fn);
      this.next = c;
      return c;
    }

    public static <S, T> Completion<S, T> from(ListenableFuture<T> lf) {
      Completion<S, T> c = new Completion<>();
      lf.addCallback(s -> {
        c.complete(s);
      }, e -> {
        c.error(e);
      });
      return c;
    }

    void error(Throwable e) {
      if (next != null) next.error(e);
    }

    void complete(T s) {
      if (next != null) {
        next.run(s);
      }
    }

    void run(S value) {
    }
  }

  @Service
  public static class MyService {

//    @Async
//    public ListenableFuture<String> work(String req) {
//      return new AsyncResult<>(req + "/asyncwork");
//    }
    public String work(String req) {
      return req + "/asyncwork";
    }
  }

  @Bean
  public ThreadPoolTaskExecutor myThreadPool() {
    ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
    te.setCorePoolSize(1);
    te.setMaxPoolSize(1);
    te.initialize();
    return te;
  }

  public static void main(String[] args) {
    SpringApplication.run(AsyncRestApp.class, args);
  }
}
