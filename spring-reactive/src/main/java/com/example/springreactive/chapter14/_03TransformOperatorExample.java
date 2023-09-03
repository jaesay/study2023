package com.example.springreactive.chapter14;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.reactivestreams.Publisher;

import com.example.springreactive.chapter14.SampleData.CovidVaccine;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

/**
 * CHAPTER 14.4. Sequence 변환 Operator
 */
@Slf4j
public class _03TransformOperatorExample {

  /**
   * map 예제 1
   */
  private static void example14_27() {
    Flux
        .just("1-Circle", "3-Circle", "5-Circle")
        // map() Operator는 Upstream에서 emit된 데이터를 mapper Function을 사용하여 변환한 후, Downstream으로 emit한다. 그리고 map() Operator 내부에서 에러 발생 시 Sequence가 종료되지 않고 계속 진행되도록 하는 기능을 지원한다.
        .map(circle -> circle.replace("Circle", "Rectangle"))
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * map 예제 2
   */
  private static void example14_28() {
    final double buyPrice = 50_000_000;

    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        .filter(tuple -> tuple.getT1() == 2021)
        .doOnNext(data -> log.info("# doOnNext: {}", data))
        .map(tuple -> calculateProfitRate(buyPrice, tuple.getT2()))
        .subscribe(data -> log.info("# onNext: {}%", data));
  }

  /**
   * flatMap 예제 1
   */
  private static void example14_29() {
    Flux
        .just("Good", "Bad")
        // flatMap() Operator는 Upstream에서 emit된 데이터 한 건이 Inner Sequence에서 여러 건의 데이터로 변환된다. 그런데 Upstream에서 emit된 데이터는 이렇게 Inner Sequence에서 평탄화(Flatten) 작업을 거치면서 하나의 Sequence로 병합(merge)되어 Downstream으로 emit된다.
        .flatMap(feeling -> Flux
            .just("Morning", "Afternoon", "Evening")
            .map(time -> feeling + " "
                + time)) // 2(Upstream에서 emit되는 데이터 수) * 3(Inner Sequence에서 emit되는 데이터 수) = 6개의 데이터가 최종적으로 Subscriber에게 전달된다.
        .subscribe(log::info);
  }

  /**
   * flatMap 예제 2
   */
  private static void example14_30() throws InterruptedException {
    Flux
        .range(2, 8)
        // flatMap() 내부의 Inner Sequence를 비동기적으로 실행하면 데이터 emit의 순서를 보장하지 않는다.
        .flatMap(dan -> Flux
            .range(1, 9)
            .publishOn(Schedulers.parallel())
            .map(n -> dan + " * " + n + " = " + dan * n))
        .subscribe(log::info);

    Thread.sleep(100L);
  }

  private static double calculateProfitRate(final double buyPrice, Long topPrice) {
    return (topPrice - buyPrice) / buyPrice * 100;
  }

  /**
   * concat 예제 1
   */
  private static void example14_31() {
    Flux
        // concat() Operator는 파라미터로 입력되는 Publisher의 Sequence를 연결해서 데이터를 순차적으로 emit한다. 특히 먼저 입력된 Publisher의 Sequence가 종료될 때까지 나머지 Publisher의 Sequence는 subscribe되지 않고 대기하는 특성이 있다.
        .concat(Flux.just(1, 2, 3), Flux.just(4, 5))
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * concat 예제 2
   */
  private static void example14_32() {
    Flux
        .concat(
            Flux.fromIterable(getViralVector()),
            Flux.fromIterable(getMRNA()),
            Flux.fromIterable(getSubunit()))
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  private static List<Tuple2<CovidVaccine, Integer>> getViralVector() {
    return SampleData.viralVectorVaccines;
  }

  private static List<Tuple2<SampleData.CovidVaccine, Integer>> getMRNA() {
    return SampleData.mRNAVaccines;
  }

  private static List<Tuple2<CovidVaccine, Integer>> getSubunit() {
    return SampleData.subunitVaccines;
  }

  /**
   * merge 예제 1
   */
  private static void example14_33() throws InterruptedException {
    Flux
        // merge() Operator는 concat() Operator처럼 먼저 입력된 Publisher의 Sequence가 종료될 때까지 나머지 Publisher의 Sequence가 subscribe되지 않고 대기하는 것이 아니라 모든 Publisher의 Sequence가 즉시 subscribe된다. 그런데 주의해야할 것은 인터리빙 방식이라고 해서 각각의 Publisher가 emit하는 데이터를 하나씩 번갈아가며 merge한다는 것이 아니라 emit된 시간 순서대로 merge한다는 것이다.
        .merge(
            Flux.just(1, 2, 3, 4).delayElements(Duration.ofMillis(300L)),
            Flux.just(5, 6, 7).delayElements(Duration.ofMillis(500L))
        )
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(2000L);
  }

  /**
   * merge 예제 2
   */
  private static void example14_34() throws InterruptedException {
    String[] usaStates = {
        "Ohio", "Michigan", "New Jersey", "Illinois", "New Hampshire",
        "Virginia", "Vermont", "North Carolina", "Ontario", "Georgia"
    };

    Flux
        .merge(getMeltDownRecoveryMessage(usaStates))
        .subscribe(log::info);

    Thread.sleep(2000L);
  }

  private static List<Mono<String>> getMeltDownRecoveryMessage(String[] usaStates) {
    List<Mono<String>> messages = new ArrayList<>();
    for (String state : usaStates) {
      messages.add(SampleData.nppMap.get(state));
    }

    return messages;
  }

  /**
   * zip 예제 1
   */
  private static void example14_35() throws InterruptedException {
    Flux
        // zip() Operator는 파라미터로 입력되는 Publisher Sequence에서 emit된 데이터를 결합하는데, 각 Publisher가 데이터를 하나씩 emit할 때까지 기다렸다가 결합한다.
        .zip(
            Flux.just(1, 2, 3).delayElements(Duration.ofMillis(300L)),
            Flux.just(4, 5, 6).delayElements(Duration.ofMillis(500L))
        )
        .subscribe(tuple2 -> log.info("# onNext: {}", tuple2));

    Thread.sleep(2500L);
  }

  /**
   * zip 예제 2
   */
  private static void example14_36() throws InterruptedException {
    Flux
        .zip(
            Flux.just(1, 2, 3).delayElements(Duration.ofMillis(300L)),
            Flux.just(4, 5, 6).delayElements(Duration.ofMillis(500L)),
            (n1, n2) -> n1 * n2
        )
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(2500L);
  }

  /**
   * zip 예제 3
   */
  private static void example14_37() throws InterruptedException {
    getInfectedPersonsPerHour(10, 21)
        .subscribe(tuples -> {
          Tuple3<Tuple2, Tuple2, Tuple2> t3 = (Tuple3) tuples;
          int sum = (int) t3.getT1().getT2() +
              (int) t3.getT2().getT2() + (int) t3.getT3().getT2();
          log.info("# onNext: {}, {}", t3.getT1().getT1(), sum);
        });
  }

  private static Flux getInfectedPersonsPerHour(int start, int end) {
    return Flux.zip(
        Flux.fromIterable(SampleData.seoulInfected)
            .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end),
        Flux.fromIterable(SampleData.incheonInfected)
            .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end),
        Flux.fromIterable(SampleData.suwonInfected)
            .filter(t2 -> t2.getT1() >= start && t2.getT1() <= end)
    );
  }

  /**
   * and 예제
   * and() Operator는 Mono의 Complete Signal과 파라미터로 입력된 Publisher의 Complete Signal을 결합하여 새로운 Mono<Void>를 반환한다.
   * 즉, Mono와 파라미터로 입력된 Publisher의 Sequence가 모두 종료되었음을 Subscriber에게 알릴 수 있다.
   * and() Operator는 모든 작업이 끝난 시점에 최종적으로 후처리 작업을 수행이 적합한 Operator이다.
   */
  private static void example14_38() throws InterruptedException {
    Mono
        .just("Task 1")
        .delayElement(Duration.ofSeconds(1))
        .doOnNext(data -> log.info("# Mono doOnNext: {}", data))
        .and(
            Flux
                .just("Task 2", "Task 3")
                .delayElements(Duration.ofMillis(600))
                .doOnNext(data -> log.info("# Flux doOnNext: {}", data))
        )
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error),
            () -> log.info("# onComplete")
        );

    Thread.sleep(5000);
  }

  private static void example14_39() throws InterruptedException {
    restartApplicationServer()
        .and(restartDBServer())
        .subscribe(
            data -> log.info("# onNext: {}", data),
            error -> log.error("# onError:", error),
            () -> log.info("# sent an email to Administrator: All Servers are restarted successfully")
        );

    Thread.sleep(6000L);
  }

  private static Mono<String> restartApplicationServer() {
    return Mono
        .just("Application Server was restarted successfully.")
        .delayElement(Duration.ofSeconds(2))
        .doOnNext(log::info);
  }

  private static Publisher<String> restartDBServer() {
    return Mono
        .just("DB Server was restarted successfully.")
        .delayElement(Duration.ofSeconds(4))
        .doOnNext(log::info);
  }

  /**
   * collectList 예제
   */
  private static void example14_40() {
    Flux
        .just("...", "---", "...")
        .map(code -> transformMorseCode(code))
        .collectList()
        .subscribe(list -> log.info(list.stream().collect(Collectors.joining())));
  }

  private static String transformMorseCode(String morseCode) {
    return SampleData.morseCodeMap.get(morseCode);
  }

  /**
   * collectMap 예제
   */
  private static void example14_41() {
    Flux
        .range(0, 26)
        .collectMap(key -> SampleData.morseCodes[key],
            value -> transformToLetter(value))
        .subscribe(map -> log.info("# onNext: {}", map));
  }

  private static String transformToLetter(int value) {
    return Character.toString((char) ('a' + value));
  }

  public static void main(String[] args) throws InterruptedException {
    example14_41();
  }
}