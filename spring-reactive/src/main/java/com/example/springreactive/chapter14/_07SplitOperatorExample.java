package com.example.springreactive.chapter14;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.math.MathFlux;

/**
 * CHAPTER 14.8. Flux Sequence 분할을 위한 Operator
 */
@Slf4j
public class _07SplitOperatorExample {

  /**
   * window 예제 1
   *   - Upstream에서 emit되는 첫 번째 데이터부터 maxSize의 숫자만큼의 데이터를 포함하는 새로운 Flux로 분할한다.
   *   - 새롭게 생성되는 Flux를 윈도우(Window)라고 한다. - 마지막 윈도우가 포함하는 데이터는 maxSize보다 작거나 같다.
   */
  private static void example14_53() {
    Flux.range(1, 11)
        .window(3)
        .flatMap(flux -> {
          log.info("======================");
          return flux;
        })
        .subscribe(new BaseSubscriber<>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(2);
          }

          @Override
          protected void hookOnNext(Integer value) {
            log.info("# onNext: {}", value);
            request(2);
          }
        });
  }

  /**
   * window 예제 2
   */
  private static void example14_54() {
    Flux.fromIterable(SampleData.monthlyBookSales2021)
        .window(3)
        .flatMap(flux -> MathFlux.sumInt(flux))
        .subscribe(new BaseSubscriber<>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(2);
          }

          @Override
          protected void hookOnNext(Integer value) {
            log.info("# onNext: {}", value);
            request(2);
          }
        });
  }

  /**
   * buffer 예제
   *   - Upstream에서 emit되는 첫 번째 데이터부터 maxSize 숫자만큼의 데이터를 List 버퍼로 한번에 emit한다.
   *   - 마지막 버퍼가 포함하는 데이터는 maxSize보다 작거나 같다.
   *   - 높은 처리량을 요구하는 애플리케이션ㄴ 있다면, 들어오는 데이터를 순차적으로 처리하기보다는 batch insert 같은 일괄 작업에 buffer() Operator를 이용해서 성능향상을 기대할 수 있다.
   */
  private static void example14_55() {
    Flux.range(1, 95)
        .buffer(10)
        .subscribe(buffer -> log.info("# onNext: {}", buffer));
  }

  /**
   * bufferTimeout 예제
   *   - Upstream에서 emit되는 첫 번째 데이터부터 maxSize 숫자 만큼의 데이터 또는 maxTime 내에 emit된 데이터를 List 버퍼로 한번에 emit한다.
   *   - maxSize나 maxTime에서 먼저 조건에 부합할때까지 emit된 데이터를 List 버퍼로 emit한다.
   *   - 마지막 버퍼가 포함하는 데이터는 maxSize보다 작거나 같다.
   *   - buffer(maxSize) Operator의 경우, 입력으로 들어오는 데이터가 maxSize가 되기 전에 어떤 오류로 인해 들어오지 못하는 상황이 발생할 경우,
   *   애플리케이션은 maxSize가 될때까지 무한정 기다리게 된다.
   *   - 따라서 bufferTimeout(maxSize, maxTime) Operator를 사용함으로써, maxSize만큼 데이터가 입력으로 들어오지 않더라도
   *   maxTime에 도달했을 때 버퍼를 비우게 해서 애플리케이션이 무한정 기다려야 하는 상황을 방지할 수 있다.
   */
  private static void example14_56() {
    Flux
        .range(1, 20)
        .map(num -> {
          try {
            if (num < 10) {
              Thread.sleep(100L);
            } else {
              Thread.sleep(300L);
            }
          } catch (InterruptedException e) {
          }
          return num;
        })
        .bufferTimeout(3, Duration.ofMillis(400L))
        .subscribe(buffer -> log.info("# onNext: {}", buffer));
  }

  /**
   * groupBy 예제 1
   *   - emit되는 데이터를 key를 기준으로 그룹화 한 GroupedFlux를 리턴한다.
   *   - 그룹화 된 GroupedFlux로 그룹별 작업을 할 수 있다.
   */
  private static void example14_57() {
    Flux.fromIterable(SampleData.books)
        .groupBy(book -> book.getAuthorName())
        .flatMap(groupedFlux ->
            groupedFlux
                .map(book -> book.getBookName() +
                    "(" + book.getAuthorName() + ")")
                .collectList()
        )
        .subscribe(bookByAuthor ->
            log.info("# book by author: {}", bookByAuthor));
  }

  /**
   * groupBy 예제 2
   *   - groupBy(keyMapper, valueMapper) Operator
   *     - emit되는 데이터를 key를 기준으로 그룹화 한 GroupedFlux를 리턴한다.
   *     - 그룹화 된 GroupedFlux로 그룹별 작업을 할 수 있다.
   *     - valueMapper를 추가로 전달해서 그룹화 되어 emit되는 데이터의 값을 미리 다른 값으로 변경할 수 있다.
   */
  private static void example14_58() {
    Flux.fromIterable(SampleData.books)
        .groupBy(book -> book.getAuthorName(),
            book -> book.getBookName() + "(" + book.getAuthorName() + ")")
        .flatMap(groupedFlux -> groupedFlux.collectList())
        .subscribe(bookByAuthor ->
            log.info("# book by author: {}", bookByAuthor));
  }

  /**
   * groupBy 예제 3
   *  - groupBy() Operator
   *      - emit되는 데이터를 key를 기준으로 그룹화 한 GroupedFlux를 리턴한다.
   *      - 그룹화 된 GroupedFlux로 그룹별 작업을 할 수 있다.
   *      - 저자 명으로 된 도서의 가격
   */
  private static void example14_59() {
    Flux.fromIterable(SampleData.books)
        .groupBy(book1 -> book1.getAuthorName())
        .flatMap(groupedFlux ->
            Mono
                .just(groupedFlux.key())
                .zipWith(
                    groupedFlux
                        .map(book -> (int) (book.getPrice() * book.getStockQuantity() * 0.1))
                        .reduce((y1, y2) -> y1 + y2),
                    (authorName, sumRoyalty) -> authorName + "'s royalty: " + sumRoyalty)
        )
        .subscribe(log::info);
  }

  public static void main(String[] args) {
    example14_59();
  }
}
