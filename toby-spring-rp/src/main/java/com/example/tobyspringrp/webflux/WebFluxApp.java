package com.example.tobyspringrp.webflux;

import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@RestController
@Slf4j
@EnableAsync
public class WebFluxApp {
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
