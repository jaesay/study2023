package com.example.tobyspringrp;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxIntvlEx {

  public static void main(String[] args) throws InterruptedException {
    // 데이터 전체 중 데이터를 몇개만 선별해서 사용할 때 유용
    Flux.interval(Duration.ofMillis(200))
        .take(10) // 10개 받으면 종료
        .subscribe(s -> log.debug("onNext: {}", s));

    log.debug("exit");
    TimeUnit.SECONDS.sleep(5); // interval은 데몬 쓰레드에서 동작하기 떄문에 다른 유저 쓰레드가 없으면 종료.. 아마 서버가 종료되지 않는 걸 방지하기위해서 같다고 함
  }
}
