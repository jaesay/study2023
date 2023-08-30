package com.example.springreactive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

/**
 * CHAPTER 11. Context
 */
@Slf4j
public class Chapter11Example {

  /**
   * Context 기본 예제
   *  - contextWrite() Operator로 Context에 데이터 쓰기 작업을 할 수 있다.
   *  - Context.put()으로 Context에 데이터를 쓸 수 있다.
   *  - deferContextual() Operator로 Context에 데이터 읽기 작업을 할 수 있다.
   *  - Context.get()으로 Context에서 데이터를 읽을 수 있다.
   *  - transformDeferredContextual() Operator로 Operator 중간에서 Context에 데이터 읽기 작업을 할 수 있다.
   */
  private static void example11_1() throws InterruptedException {
    Mono
        .deferContextual(ctx ->
            Mono
                .just("Hello" + " " + ctx.get("firstName"))
                .doOnNext(data -> log.info("# just doOnNext : {}", data))
        )
        .subscribeOn(Schedulers.boundedElastic())
        .publishOn(Schedulers.parallel())
        .transformDeferredContextual(
            (mono, ctx) -> mono.map(data -> data + " " + ctx.get("lastName"))
        )
        .contextWrite(context -> context.put("lastName", "Jobs"))
        .contextWrite(context -> context.put("firstName", "Steve"))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(100L);
  }

  /**
   * Context API 사용 예제
   */
  private static void example11_3() throws InterruptedException {
    final String key1 = "company";
    final String key2 = "firstName";
    final String key3 = "lastName";

    Mono
        .deferContextual(ctx ->
            Mono.just(ctx.get(key1) + ", " + ctx.get(key2) + " " + ctx.get(key3))
        )
        .publishOn(Schedulers.parallel())
        .contextWrite(context ->
            context.putAll(Context.of(key2, "Steve", key3, "Jobs").readOnly())
        )
        .contextWrite(context -> context.put(key1, "Apple"))
        .subscribe(data -> log.info("# onNext: {}" , data));

    Thread.sleep(100L);
  }

  /**
   * ContextView API 사용 예제
   */
  private static void example11_4() throws InterruptedException {
    final String key1 = "company";
    final String key2 = "firstName";
    final String key3 = "lastName";

    Mono
        .deferContextual(ctx ->
            Mono.just(ctx.get(key1) + ", " +
                ctx.getOrEmpty(key2).orElse("no firstName") + " " +
                ctx.getOrDefault(key3, "no lastName"))
        )
        .publishOn(Schedulers.parallel())
        .contextWrite(context -> context.put(key1, "Apple"))
        .subscribe(data -> log.info("# onNext: {}" , data));

    Thread.sleep(100L);
  }

  /**
   * Context의 특징 예제
   *   - Context는 각각의 구독을 통해 Reactor Sequence에 연결 되며 체인의 각 Operator는 연결된 Context에 접근할 수 있어야 한다.
   */
  private static void example11_5() throws InterruptedException {
    final String key1 = "company";

    Mono<String> mono = Mono.deferContextual(ctx ->
            Mono.just("Company: " + " " + ctx.get(key1))
        )
        .publishOn(Schedulers.parallel());


    mono.contextWrite(context -> context.put(key1, "Apple"))
        .subscribe(data -> log.info("# subscribe1 onNext: {}", data));

    mono.contextWrite(context -> context.put(key1, "Microsoft"))
        .subscribe(data -> log.info("# subscribe2 onNext: {}", data));

    Thread.sleep(100L);
  }

  /**
   * Context의 특징 예제
   *  - Context는 Operator 체인의 아래에서부터 위로 전파된다.
   *      - 따라서 Operator 체인 상에서 Context read 메서드가 Context write 메서드 밑에 있을 경우에는 write된 값을 read할 수 없다.
   */
  private static void example11_6() throws InterruptedException {
    String key1 = "company";
    String key2 = "name";

    Mono
        .deferContextual(ctx ->
            Mono.just(ctx.get(key1))
        )
        .publishOn(Schedulers.parallel())
        .contextWrite(context -> context.put(key2, "Bill")) // Context는 Operator 체인상의 아래에서 위로 전파되는 특징이 있기 때문에 contextWrite()을 Operator 체인의 맨 마지막에 두는 것이 좋다.
        .transformDeferredContextual((mono, ctx) ->
            mono.map(data -> data + ", " + ctx.getOrDefault(key2, "Steve")) // Context는 Operator 체인의 아래에서부터 위로 전파되기 때문에 key2의 값이 존재하지 않는다.
        )
        .contextWrite(context -> context.put(key1, "Apple"))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(100L);
  }

  /**
   * Context의 특징
   *  - inner Sequence 내부에서는 외부 Context에 저장된 데이터를 읽을 수 있다.
   *  - inner Sequence 외부에서는 inner Sequence 내부 Context에 저장된 데이터를  읽을 수 없다.
   */
  private static void example11_7() throws InterruptedException {
    String key1 = "company";

    Mono
        .just("Steve")
//        .transformDeferredContextual((stringMono, ctx) -> ctx.get("role")) // NoSuchElementException 발생: inner Sequence 외부에서는 inner Sequence 내부 Context에 저장된 데이터를  읽을 수 없다.
        .flatMap(name ->
            Mono.deferContextual(ctx ->
                Mono
                    .just(ctx.get(key1) + ", " + name)
                    .transformDeferredContextual((mono, innerCtx) ->
                        mono.map(data -> data + ", " + innerCtx.get("role"))
                    )
                    .contextWrite(context -> context.put("role", "CEO"))
            )
        )
        .publishOn(Schedulers.parallel())
        .contextWrite(context -> context.put(key1, "Apple"))
        .subscribe(data -> log.info("# onNext: {}", data));

    Thread.sleep(100L);
  }

  public static final String HEADER_AUTH_TOKEN = "authToken";

  /**
   * Context 활용 예제
   *  - 인증 정보 같은 직교성을 가지는 정보를 표현할 때 주로 사용된다.
   */
  private static void example11_8() {
    Mono<String> mono =
        postBook(Mono.just(
            new Book("abcd-1111-3533-2809"
                , "Reactor's Bible"
                ,"Kevin"))
        )
            .contextWrite(Context.of(HEADER_AUTH_TOKEN, "eyJhbGciOi"));

    mono.subscribe(data -> log.info("# onNext: {}", data));
  }

  private static Mono<String> postBook(Mono<Book> book) {
    return Mono
        .zip(book,
            Mono
                .deferContextual(ctx ->
                    Mono.just(ctx.get(HEADER_AUTH_TOKEN)))
        )
        .flatMap(tuple -> {
          String response = "POST the book(" + tuple.getT1().getBookName() +
              "," + tuple.getT1().getAuthor() + ") with token: " +
              tuple.getT2();
          return Mono.just(response); // HTTP POST 전송을 했다고 가정
        });
  }

  @AllArgsConstructor
  @Data
  static class Book {
    private String isbn;
    private String bookName;
    private String author;
  }

  public static void main(String[] args) throws InterruptedException {
    example11_8();
  }
}
