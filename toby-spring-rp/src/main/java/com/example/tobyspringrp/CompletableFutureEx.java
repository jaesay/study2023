package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CompletableFutureEx {

  public static void main(String[] args) throws InterruptedException {
    ExecutorService es = Executors.newFixedThreadPool(10);

    CompletableFuture
        .supplyAsync(() -> {
          log.info("runAsync");
//          if (1 == 1) throw new RuntimeException();
          return 1;
        }, es)
        .thenCompose(s -> {
          log.info("thenCompose {}", s);
          return CompletableFuture.completedFuture(s + 1);
        })
        .thenApplyAsync(s2 -> {
          log.info("thenApply {}", s2);
          return s2 * 3;
        }, es)
        .exceptionally(e -> -10)
        .thenAccept(s3 -> log.info("thenAccept {}", s3));

    log.info("exit");

    ForkJoinPool.commonPool().shutdown();
    ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
  }
}
