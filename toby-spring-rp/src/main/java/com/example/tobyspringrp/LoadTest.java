package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LoadTest {

  static AtomicInteger counter = new AtomicInteger(0);

  public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
    ExecutorService es = Executors.newFixedThreadPool(100);

    RestTemplate restTemplate = new RestTemplate();
    String url = "http://localhost:8080/rest?idx={idx}";

    // for 문을 통해 순차적 호출이 아닌 실제 동시 호출을 위해 사용
    CyclicBarrier barrier = new CyclicBarrier(101);

    StopWatch main = new StopWatch();
    main.start();

    for (int i = 0; i < 100; i++) {
      es.submit(() -> {
        int idx = counter.addAndGet(1);

        barrier.await();

        log.info("Thread {}", idx);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        String res = restTemplate.getForObject(url, String.class, idx);

        stopWatch.stop();
        log.info("Elapsed: {} {} / {}", idx, stopWatch.getTotalTimeSeconds(), res);
        return null;
      });
    }

    barrier.await();
    es.shutdown();
    es.awaitTermination(100, TimeUnit.SECONDS);

    main.stop();
    log.info("Total: {}", main.getTotalTimeSeconds());
  }
}
