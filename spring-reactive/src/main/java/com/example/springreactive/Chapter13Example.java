package com.example.springreactive;

import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Chapter13Example {

  public static class GeneralTestExample {
    public static Flux<String> sayHello() {
      return Flux
          .just("Hello", "Reactor");
    }

    public static Flux<Integer> divideByTwo(Flux<Integer> source) {
      return source
          .zipWith(Flux.just(2, 2, 2, 2, 0), (x, y) -> x/y);
    }

    public static Flux<Integer> takeNumber(Flux<Integer> source, long n) {
      return source
          .take(n);
    }
  }

  public static class TimeBasedTestExample {

    public static Flux<Tuple2<String, Integer>> getCOVID19Count(Flux<Long> source) {
      return source
          .flatMap(notUse -> Flux.just(
                  Tuples.of("서울", 10),
                  Tuples.of("경기도", 5),
                  Tuples.of("강원도", 3),
                  Tuples.of("충청도", 6),
                  Tuples.of("경상도", 5),
                  Tuples.of("전라도", 8),
                  Tuples.of("인천", 2),
                  Tuples.of("대전", 1),
                  Tuples.of("대구", 2),
                  Tuples.of("부산", 3),
                  Tuples.of("제주도", 0)
              )
          );
    }

    public static Flux<Tuple2<String, Integer>> getVoteCount(Flux<Long> source) {
      return source
          .zipWith(Flux.just(
                  Tuples.of("중구", 15400),
                  Tuples.of("서초구", 20020),
                  Tuples.of("강서구", 32040),
                  Tuples.of("강동구", 14506),
                  Tuples.of("서대문구", 35650)
              )
          )
          .map(Tuple2::getT2);
    }
  }

  public static class BackpressureTestExample {

    public static Flux<Integer> generateNumber() {
      return Flux
          .create(emitter -> {
            for (int i = 1; i <= 100; i++) {
              emitter.next(i);
            }
            emitter.complete();
          }, FluxSink.OverflowStrategy.ERROR);
    }
  }

  public static class ContextTestExample {

    public static Mono<String> getSecretMessage(Mono<String> keySource) {
      return keySource
          .zipWith(Mono.deferContextual(ctx ->
              Mono.just((String)ctx.get("secretKey"))))
          .filter(tp ->
              tp.getT1().equals(
                  new String(Base64Utils.decodeFromString(tp.getT2())))
          )
          .transformDeferredContextual(
              (mono, ctx) -> mono.map(notUse -> ctx.get("secretMessage"))
          );
    }
  }

  public static class RecordTestExample {

    public static Flux<String> getCapitalizedCountry(Flux<String> source) {
      return source
          .map(country -> country.substring(0, 1).toUpperCase() + country.substring(1));
    }
  }

  public static class PublisherProbeTestExample {
    public static Mono<String> processTask(Mono<String> main, Mono<String> standby) {
      return main
          .flatMap(Mono::just)
          .switchIfEmpty(standby); // switchIfEmpty() Operator는 Upstream Publisher가 데이터 emit 없이 종룓든 경우, 대체 Publisher가 데이터를 emit한다.
    }

    public static Mono<String> supplyMainPower() {
      return Mono.empty();
    }

    public static Mono<String> supplyStandbyPower() {
      return Mono.just("# supply Standby Power");
    }
  }
}