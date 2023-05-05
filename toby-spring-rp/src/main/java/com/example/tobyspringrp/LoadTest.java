package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

  static AtomicInteger counter = new AtomicInteger(0);

  public static void main(String[] args) throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(100);

    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8080/dr";

    StopWatch main = new StopWatch();
    main.start();

    for (int i = 0; i < 100; i++) {
      es.execute(() -> {
        int idx = counter.addAndGet(1);
        log.info("Thread {}", idx);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        restTemplate.getForObject(url, String.class);

        stopWatch.stop();
        log.info("Elapsed: {} {}", idx, stopWatch.getTotalTimeSeconds());
      });
    }

    es.shutdown();
    es.awaitTermination(100, TimeUnit.SECONDS);

    main.stop();
    log.info("Total: {}", main.getTotalTimeSeconds());
  }
}
