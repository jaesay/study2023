package com.example.springreactive.chapter14;

import java.time.Duration;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import com.example.springreactive.chapter14.SampleData.Book;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CHAPTER 14.6 예외 처리를 위한 Operator
 */
@Slf4j
public class _05ErrorOperatorExample {

  /**
   * error 예제 1
   *   - 명시적으로 error 이벤트를 발생시켜야 하는 경우
   *   - flatMap처럼 Inner Sequence가 존재하는 경우 체크 예외 발생 시 Flux로 래핑해서 onError Signal을 전송할 수 있다.
   */
  private static void example14_43() {
    Flux
        .range(1, 5)
        .flatMap(num -> {
          if ((num * 2) % 3 == 0) {
            return Flux.error(
                new IllegalArgumentException("Not allowed multiple of 3"));
          } else {
            return Mono.just(num * 2);
          }
        })
        .subscribe(data -> log.info("# onNext: {}", data),
            error -> log.error("# onError: ", error));
  }

  /**
   * error 처리 예제 2
   *   - 명시적으로 error 이벤트를 발생시켜야 하는 경우
   *   - flatMap처럼 Inner Sequence가 존재하는 경우 체크 예외 발생 시 Flux로 래핑해서 onError Signal을 전송할 수 있다.
   */
  private static void example14_44() {
    Flux
        .just('a', 'b', 'c', '3', 'd')
        .flatMap(letter -> {
          try {
            return convert(letter);
          } catch (DataFormatException e) {
            return Flux.error(e);
          }
        })
        .subscribe(data -> log.info("# onNext: {}", data),
            error -> log.error("# onError: ", error));
  }

  private static Mono<String> convert(char ch) throws DataFormatException {
    if (!Character.isAlphabetic(ch)) {
      throw new DataFormatException("Not Alphabetic");
    }
    return Mono.just("Converted to " + Character.toUpperCase(ch));
  }

  /**
   * onErrorReturn 예제 1
   *   - 예외가 발생했을 때, error 이벤트를 발생시키지 않고, default value로 대체해서 emit하고자 할 경우
   *   - try ~ catch 문의 경우, catch해서 return default value 하는 것과 같다.
   */
  private static void example14_45() {
    getBooks()
        .map(book -> book.getPenName().toUpperCase())
        .onErrorReturn("No pen name")
        .subscribe(log::info);
  }

  public static Flux<Book> getBooks() {
    return Flux.fromIterable(SampleData.books);
  }

  /**
   * onErrorReturn 예제 2
   */
  private static void example14_46() {
    getBooks()
        .map(book -> book.getPenName().toUpperCase())
        .onErrorReturn(NullPointerException.class, "no pen name")
        .onErrorReturn(IllegalFormatException.class, "Illegal pen name")
        .subscribe(log::info);
  }

  /**
   * onErrorResume 예제
   *   - 예외가 발생했을 때, error 이벤트를 발생시키지 않고, 대체 Publisher로 데이터를 emit하고자 할 경우
   *   - try ~ catch 문의 경우, catch해서 return default value 하는 것과 같다.
   */
  private static void example14_47() {
    final String keyword = "DDD";
    getBooksFromCache(keyword)
        .onErrorResume(error -> getBooksFromDatabase(keyword))
        .subscribe(data -> log.info("# onNext: {}", data.getBookName()),
            error -> log.error("# onError: ", error));
  }

  private static Flux<Book> getBooksFromCache(final String keyword) {
    return Flux
        .fromIterable(SampleData.books)
        .filter(book -> book.getBookName().contains(keyword))
        .switchIfEmpty(Flux.error(new NoSuchBookException("No such Book")));
  }

  private static Flux<Book> getBooksFromDatabase(final String keyword) {
    List<Book> books = new ArrayList<>(SampleData.books);
    books.add(new Book("DDD: Domain Driven Design",
        "Joy", "ddd-man", 35000, 200));

    return Flux
        .fromIterable(books)
        .filter(book -> book.getBookName().contains(keyword))
        .switchIfEmpty(Flux.error(new NoSuchBookException("No such Book")));
  }

  private static class NoSuchBookException extends RuntimeException {
    NoSuchBookException(String message) {
      super(message);
    }
  }

  /**
   * onErrorContinue 예제
   *   - 예외가 발생했을 때, 예외를 발생시킨 데이터를 건너뛰고 Upstream에서 emit된 다음 데이터를 처리한다.
   *   - Reactor 공식 문서에서는 onErrorContinue() Operator가 명확하지 않은 Sequence의 동작으로 개발자가 의도하지 않은 상황을
   *   발생시킬 수 있기 때문에 onErrorContinue() Operator를 신중하게 사용하기를 권고한다..
   *   대부분의 에러는 Operator 내부에서 doOnError() Operator를 통해 로그를 기록하고 onErrorResume() Operator 등으로 처리할
   *   수 있다고 명시한다.
   */
  private static void example14_48() {
    Flux
        .just(1, 2, 4, 0, 6, 12)
        .map(num -> 12 / num)
        .onErrorContinue((error, num) -> log.error("error: {}, num: {}", error, num))
        .subscribe(data -> log.info("# onNext: {}", data),
            error -> log.error("# onError: ", error));
  }

  /**
   * retry 예제 1
   *   - 에러가 발생했을 때, 지정된 횟수만큼 Sequence를 다시 구독한다.
   */
  private static void example14_49() throws InterruptedException {
    final int[] count = {1};
    Flux
        .range(1, 3)
        .delayElements(Duration.ofSeconds(1))
        .map(num -> {
          try {
            if (num == 3 && count[0] == 1) {
              count[0]++;
              Thread.sleep(1000);
            }
          } catch (InterruptedException e) {}

          return num;
        })
        .timeout(Duration.ofMillis(1500))
        .retry(1)
        .subscribe(data -> log.info("# onNext: {}", data),
            (error -> log.error("# onError: ", error)),
            () -> log.info("# onComplete"));

    Thread.sleep(7000);
  }

  /**
   * retry 예제 2
   */
  private static void example14_50() throws InterruptedException {
    getBooks14_50()
        .collect(Collectors.toSet())
        .subscribe(bookSet -> bookSet
            .forEach(book -> log.info("book name: {}, price: {}", book.getBookName(), book.getPrice())));

    Thread.sleep(12000);
  }

  private static Flux<Book> getBooks14_50() {
    final int[] count = {0};
    return Flux
        .fromIterable(SampleData.books)
        .delayElements(Duration.ofMillis(500))
        .map(book -> {
          try {
            count[0]++;
            if (count[0] == 3) {
              Thread.sleep(2000);
            }
          } catch (InterruptedException e) {
          }

          return book;
        })
        .timeout(Duration.ofSeconds(2))
        .retry(1)
        .doOnNext(book -> log.info("# getBooks > doOnNext: {}, price: {}", book.getBookName(), book.getPrice()));
  }

  public static void main(String[] args) throws InterruptedException {
    example14_50();
  }
}