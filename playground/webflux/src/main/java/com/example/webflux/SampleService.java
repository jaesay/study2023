package com.example.webflux;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SampleService {

  public Mono<Sample> getSample_1() {
    Sample sample = new Sample();
    return Mono.zip(getProp1_1(), getProp1_2())
        .flatMap(t1 -> {
          sample.setProp1_1(t1.getT1());
          sample.setProp1_2(t1.getT2());
          return Mono.zip(
              getProp2_1(),
              getProp2_2_1().flatMap(prop2_2_1 -> {
                sample.setProp2_2_1(prop2_2_1);
                return getProp2_2_2();
              })
          );
        })
        .flatMap(t2 -> {
          sample.setProp2_1(t2.getT1());
          sample.setProp2_2_2(t2.getT2());
          return getProp3();
        })
        .map(prop3 -> {
          sample.setProp3(prop3);
          return sample;
        });
  }

  private Mono<String> getProp1_1() {
    return Mono.just("prop1_1")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp1_2() {
    return Mono.just("prop1_2")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp2_1() {
    return Mono.just("prop2_1")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp2_2_1() {
    return Mono.just("prop2_2_1")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp2_2_2() {
    return Mono.just("prop2_2_2")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp3() {
    return Mono.just("prop3")
        .doOnNext(System.out::println);
  }
}
