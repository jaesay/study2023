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
              getProp2_1(t1.getT1(), t1.getT2()),
              getSubProp2_2(t1.getT1(), t1.getT2()).flatMap(this::getProp2_2)
          );
        })
        .flatMap(t2 -> {
          sample.setProp2_1(t2.getT1());
          sample.setProp2_2(t2.getT2());
          return getProp3(t2.getT1(), t2.getT2());
        })
        .map(prop3 -> {
          sample.setProp3(prop3);
          return sample;
        });
  }

  public Mono<Sample> getSample_2() {
    return Mono.zip(getProp1_1(), getProp1_2())
        .flatMap(t1 -> Mono.zip(
            Mono.just(t1.getT1()),
            Mono.just(t1.getT2()),
            getProp2_1(t1.getT1(), t1.getT2()),
            getSubProp2_2(t1.getT1(), t1.getT2()).flatMap(this::getProp2_2))
        )
        .flatMap(t2 -> Mono.zip(
            Mono.just(t2.getT1()),
            Mono.just(t2.getT2()),
            Mono.just(t2.getT3()),
            Mono.just(t2.getT4()),
            getProp3(t2.getT3(), t2.getT4())
        ))
        .map(t3 -> new Sample(
            t3.getT1(),
            t3.getT2(),
            t3.getT3(),
            t3.getT4(),
            t3.getT5()));
  }

  private Mono<String> getProp1_1() {
    return Mono.just("prop1_1")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp1_2() {
    return Mono.just("prop1_2")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp2_1(String prop1_1, String prop1_2) {
    return Mono.just("prop2_1")
        .doOnNext(System.out::println);
  }

  private Mono<String> getSubProp2_2(String prop1_1, String prop1_2) {
    return Mono.just("subProp2_2")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp2_2(String subProp2_2) {
    return Mono.just("prop2_2")
        .doOnNext(System.out::println);
  }

  private Mono<String> getProp3(String prop2_1, String prop2_2) {
    return Mono.just("prop3")
        .doOnNext(System.out::println);
  }
}
