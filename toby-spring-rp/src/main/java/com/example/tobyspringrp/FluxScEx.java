package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxScEx {

  public static void main(String[] args) {
    Flux.range(1, 10)
        .publishOn(Schedulers.newSingle("pub"))
        .log()
//        .subscribeOn(Schedulers.newSingle("sub"))
        .subscribe(System.out::println);

    log.debug("exit");
  }
}
