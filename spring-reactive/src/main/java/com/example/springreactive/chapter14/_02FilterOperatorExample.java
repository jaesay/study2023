package com.example.springreactive.chapter14;

import static com.example.springreactive.chapter14.SampleData.getCovidVaccines;

import com.example.springreactive.chapter14.SampleData.CovidVaccine;
import java.time.Duration;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

/**
 * CHAPTER 14. Sequence 필터링 Operator
 */
@Slf4j
public class _02FilterOperatorExample {

  /**
   * filter 예제 1
   */
  private static void example14_15() {
    Flux
        .range(1, 20)
        .filter(num -> num % 2 != 0)
        .subscribe(data -> log.info("# onNext: {}", data));
  }

  /**
   * filter 예제 2
   */
  private static void example14_16() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        .filter(tuple -> tuple.getT2() > 20_000_000)
        .subscribe(data -> log.info(data.getT1() + ":" + data.getT2()));
  }

  /**
   * filterWhen 예제
   */
  private static void example14_17() throws InterruptedException {
    Map<CovidVaccine, Tuple2<CovidVaccine, Integer>> vaccineMap = getCovidVaccines();

    Flux
        .fromIterable(SampleData.coronaVaccineNames)
        // filterWhen() Operator는 내부에서 Inner Sequence를 통해 조건에 맞는 데이터인지를 비동기적으로 테스트한 후, 테스트 결과가 true라면 filterWhen()의 Upstream으로부터 전달받은 데이터를 Downstream으로 emit한다.
        .filterWhen(vaccine -> Mono
            .just(vaccineMap.get(vaccine).getT2() >= 3_000_000)
            .publishOn(Schedulers.parallel()))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(1000);
  }

  /**
   * skip 예제 1
   */
  private static void example14_18() throws InterruptedException {
    Flux
        .interval(Duration.ofSeconds(1))
        .skip(2)
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(5500L);
  }

  /**
   * skip 예제 2
   */
  private static void example14_19() throws InterruptedException {
    Flux
        .interval(Duration.ofMillis(300))
        .skip(Duration.ofSeconds(1))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(2000L);
  }

  /**
   * skip 예제 3
   */
  private static void example14_20() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        .filter(tuple -> tuple.getT2() >= 20_000_000)
        .skip(2)
        .subscribe(tuple -> log.info("{}, {}", tuple.getT1(), tuple.getT2()));
  }

  /**
   * take 예제 1
   */
  private static void example14_21() throws InterruptedException {
    Flux
        .interval(Duration.ofSeconds(1))
        // skip() Operator가 Upstream에서 emit된 데이터 중에서 파라미터로 입력받은 숫자만큼 건너뛴 후 나머지 데이터를 Downstream으로 emit하는 반면에, take() Operator는 Upstream에서 emit되는 데이터 중에서 파라미터로 입력받은 숫자만큼만 Downstream으로 emit한다.
        .take(3)
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(4000L);
  }

  /**
   * take 예제 2
   */
  private static void example14_22() throws InterruptedException {
    Flux
        .interval(Duration.ofSeconds(1))
        // take() Operator의 파라미터로 시간을 지정하면 Upstream에서 emit되는 데이터 중에서 파라미터로 입력한 시간 내에 emit된 데이터만 Downstream으로 emit한다.
        .take(Duration.ofMillis(2500))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(3000L);
  }

  /**
   * takeLast 예제
   */
  private static void example14_23() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        // takeLast() Operator는 Upstream에서 emit된 데이터 중에서 파라미터로 입력한 개수만큼 가장 마지막에 emit된 데이터를 Downstream으로 emit한다.
        .takeLast(2)
        .subscribe(tuple -> log.info("# onNext: {}, {}", tuple.getT1(), tuple.getT2()));
  }

  /**
   * takeUntil 예제
   */
  private static void example14_24() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        // takeUntil() Operator는 파라미터로 입력한 람다 표현식(Predicate)이 true가 될 때까지 Upstream에서 emit된 데이터를 Downstream으로 emit한다. Upstream에서 emit된 데이터에는 Predicate을 평가할 때 사용한 데이터가 포함된다.
        .takeUntil(tuple -> tuple.getT2()
            > 20_000_000) // 20_000_000를 초과한 T2가 있을 때까지 emit하며 20_000_000 초과한 첫번째 데이터도 포함된다.
        .subscribe(tuple -> log.info("# onNext: {}, {}", tuple.getT1(), tuple.getT2()));
  }

  /**
   * takeWhile 예제
   */
  private static void example14_25() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        // takeWhile() Operator는 takeUtil() Operator와 달리 파라미터로 입력한 람다 표현식(Predicate)이 true가 되는 동안에만 Upstream에서 emit된 데이터를 Downstream으로 emit한다. 즉, Upstream에서 emit된 데이터가 false라면 Sequence가 종료된다. takeWhile() Operator는 Predecate을 평가할 때 사용한 데이터가 Downstream으로 emit되지 않는다.
        .takeWhile(tuple -> tuple.getT2() < 20_000_000) // T2가 20_000_000 이상인 첫번째 데이터는 포함되지 않는다.
        .subscribe(tuple -> log.info("# onNext: {}, {}", tuple.getT1(), tuple.getT2()));
  }

  /**
   * next 예제
   */
  private static void example14_26() {
    Flux
        .fromIterable(SampleData.btcTopPricesPerYear)
        // next() Operator는 Upstream에서 emit되는 데이터 중에서 첫번째 데이터만 Downstram으로 emit한다. 만일 Upstream에서 emit되는 데이터가 empty라면 Downstream으로 empty Mono를 emit한다.
        .next()
        .subscribe(tuple -> log.info("# onNext: {}, {}", tuple.getT1(), tuple.getT2()));
  }

  public static void main(String[] args) throws InterruptedException {
    example14_26();
  }
}
