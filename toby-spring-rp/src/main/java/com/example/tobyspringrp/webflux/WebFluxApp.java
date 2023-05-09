package com.example.tobyspringrp.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@RestController
@Slf4j
@EnableAsync
public class WebFluxApp {

  @GetMapping("/events/{id}")
  Mono<List<Event>> events(@PathVariable long id) {
    List<Event> events = List.of(new Event(1L, "event1"), new Event(2L, "event2"));
    return Mono.just(events);
  }

  @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  Flux<Event> events() {
    Flux<String> es = Flux.generate(sink -> sink.next("value"));
    Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

    return Flux.zip(es, interval)
        .log()
        .map(tu -> new Event(tu.getT2(), tu.getT1()))
        .take(10);
  }

  @Data @AllArgsConstructor
  public static class Event {
    private Long id;
    private String value;
  }

  @GetMapping("/")
  Mono<String> hello() {
    log.info("pos1");
//    Mono<String> m = Mono.just(generateHello()).doOnNext(c -> log.info(c)).log();
    Mono<String> m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info(c)).log();
    m.subscribe();
    log.info("pos2");
    return m;
  }

  private String generateHello() {
    log.info("generateHello()");
    return "Hello Mono";
  }


  static final String URL1 = "http://localhost:8081/service?req={req}";
  static final String URL2 = "http://localhost:8081/service2?req={req}";

  WebClient webClient = WebClient.builder()
      .clientConnector(new ReactorClientHttpConnector(
          HttpClient.create().runOn(new NioEventLoopGroup(1)))
      ).build();

  @Autowired
  MyService myService;

  @GetMapping("rest")
  public Mono<String> rest(int idx) {
    return webClient.get().uri(URL1, idx).exchange()
        .flatMap(c -> c.bodyToMono(String.class))
        .flatMap(res1 -> webClient.get().uri(URL2, res1).exchange())
        .flatMap(c -> c.bodyToMono(String.class))
        .doOnNext(c -> log.info(c))
        .flatMap(res2 -> Mono.fromCompletionStage(myService.work(res2)))
        .doOnNext(c -> log.info(c));
  }

  @Service
  public static class MyService {

    @Async
    public CompletableFuture<String> work(String req) {
      return CompletableFuture.completedFuture(req + "/asyncwork");
    }
  }

  public static void main(String[] args) {
    SpringApplication.run(WebFluxApp.class, args);
  }
}
