package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {

  public Flux<String> namesFlux() {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .log();
  }

  public Flux<String> namesFlux_map(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .map(name -> name.length() + "-" + name)
        .log();
  }

  public Flux<String> namesFlux_flatmap(int length) {
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .flatMap(this::splitString)
        .log();
  }

  private Flux<String> splitString(String name) {
    return Flux.fromArray(name.split(""));
  }

  public Flux<String> namesFlux_flatmap_async(int length) {
    // 비동기로 호출하며 순서를 보존하지 않음, 더 빠름
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .flatMap(this::splitStringWithDelay)
        .log();
  }

  public Flux<String> namesFlux_concatmap(int length) {
    // flapMap 과 비슷하지만 순서보존 및 동기
    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .concatMap(this::splitStringWithDelay)
        .log();
  }

  public Flux<String> namesFlux_transform(int length) {
    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > length);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        .concatMap(this::splitString)
        .log();
  }

  public Flux<String> namesFlux_defaultIfEmpty(int length) {
    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > length);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        .defaultIfEmpty("default")
        .log();
  }

  public Flux<String> namesFlux_switchIfEmpty(int length) {
    Function<Flux<String>, Flux<String>> filterMap = name -> name.map(String::toUpperCase)
        .filter(s -> s.length() > length)
        .flatMap(this::splitString);

    Flux<String> defaultFlux = Flux.just("default")
        .transform(filterMap);

    return Flux.fromIterable(List.of("alex", "ben", "chloe"))
        .transform(filterMap)
        .log()
        .switchIfEmpty(defaultFlux)
        .log();
  }

  private Flux<String> splitStringWithDelay(String name) {
//    int delay = new Random().nextInt(1000);
    return Flux.fromArray(name.split(""))
        .delayElements(Duration.ofMillis(1000));
  }

  public Flux<String> namesFlux_immutable() {
    Flux<String> namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));
    namesFlux.map(String::toUpperCase); // immutable nature
    return namesFlux;
  }

  public Mono<String> nameMono() {
    return Mono.just("alex")
        .log();
  }

  public Mono<List<String>> nameMono_flatMap(int length) {
    return Mono.just("alex")
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .flatMap(this::splitStringMono)
        .log();
  }

  private Mono<List<String>> splitStringMono(String name) {
    return Mono.just(List.of(name.split("")));
  }

  public Flux<String> nameMono_flatMapMany(int length) {
    return Mono.just("alex")
        .map(String::toUpperCase)
        .filter(name -> name.length() > length)
        .flatMapMany(this::splitString)
        .log();
  }

  public Flux<String> explore_concat() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");
    Flux<String> defFlux = Flux.just("D", "E", "F");

    // Flux의 static method 이며 publisher들은 순차적으로 subscribe 된다.
    return Flux.concat(abcFlux, defFlux).log();
  }

  public Flux<String> explore_concatWith() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");
    Flux<String> defFlux = Flux.just("D", "E", "F");

    // flux의 concat과 같지만 instance method
    return abcFlux.concatWith(defFlux).log();
  }

  public Flux<String> explore_concatWith_mono() {
    Mono<String> aMono = Mono.just("A");
    Mono<String> bMono = Mono.just("B");

    // flux의 concat과 같지만 instance method
    return aMono.concatWith(bMono).log();
  }

  public Flux<String> explore_merge() {
    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100));

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(125));

    // Flux의 static method 이며 publisher들은 동시에 subscribe 된다.
    return Flux.merge(abcFlux, defFlux).log();
  }

  public Flux<String> explore_mergeWith() {
    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100));

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(125));

    // flux의 merge 같지만 instance method
    return abcFlux.mergeWith(defFlux).log();
  }

  public Flux<String> explore_mergerWith_mono() {
    Mono<String> aMono = Mono.just("A");
    Mono<String> bMono = Mono.just("B");

    // flux의 merge과 같지만 instance method
    return aMono.mergeWith(bMono).log();
  }

  public Flux<String> explore_mergeSequential() {
    Flux<String> abcFlux = Flux.just("A", "B", "C")
        .delayElements(Duration.ofMillis(100));

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(125));

    // Flux의 static method 이며 publisher들은 동시에 subscribe 하고 순서를 보존한다.
    return Flux.mergeSequential(abcFlux, defFlux).log();
  }

  public Flux<String> explore_zip() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");

    Flux<String> defFlux = Flux.just("D", "E", "F")
        .delayElements(Duration.ofMillis(1000));

    return Flux.zip(abcFlux, defFlux, (first, second) -> first + second).log();
  }

  public Flux<String> explore_zip2() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");

    Flux<String> defFlux = Flux.just("D", "E", "F");
    Flux<String> _123Flux = Flux.just("1", "2", "3");
    Flux<String> _456Flux = Flux.just("4", "5", "6");

    return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
        .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
        .log();
  }

  public Flux<String> explore_zipWith() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");

    Flux<String> defFlux = Flux.just("D", "E", "F");

    return abcFlux.zipWith(defFlux, (first, second) -> first + second).log();
  }

  public Flux<String> explore_zipWith2() {
    Flux<String> abcFlux = Flux.just("A", "B", "C");

    Flux<String> defFlux = Flux.just("D", "E", "F");

    return abcFlux.zipWith(defFlux)
        .map(t2 -> t2.getT1() + t2.getT2())
        .log();
  }

  public Mono<String> explore_zipWith_mono() {
    Mono<String> aMono = Mono.just("A");
    Mono<String> bMono = Mono.just("B");

    return aMono.zipWith(bMono, (first, second) -> first + second).log();
  }

  public Mono<String> explore_zipWith_mono2() {
    Mono<String> aMono = Mono.just("A");
    Mono<String> bMono = Mono.just("B");

    return aMono.zipWith(bMono)
        .map(t2 -> t2.getT1() + t2.getT2())
        .log();
  }

  public static void main(String[] args) {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
    fluxAndMonoGeneratorService.namesFlux()
        .subscribe(name -> System.out.println("Name is : " + name));

    fluxAndMonoGeneratorService.nameMono()
        .subscribe(name -> System.out.println("Name is : " + name));
    System.out.println("exit");
  }
}
