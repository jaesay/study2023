package com.example.springreactive;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public class Chapter07Example {

  /**
   * Cold Sequence 예제
   */
  private static void example7_1() throws InterruptedException {
    Flux<String> coldFlux =
        Flux
            .fromIterable(Arrays.asList("KOREA", "JAPAN", "CHINESE"))
            .map(String::toLowerCase);

    coldFlux.subscribe(country -> log.info("# Subscriber1: {}", country));
    System.out.println("----------------------------------------------------------------------");
    Thread.sleep(2000L);
    // 구독이 발생할 때마다 emit된 데이터를 처음부터 다시 전달한다.
    coldFlux.subscribe(country -> log.info("# Subscriber2: {}", country));
  }

  /**
   * Hot Sequence 예제
   */
  private static void example7_2() throws InterruptedException {
    String[] singers = {"Singer A", "Singer B", "Singer C", "Singer D", "Singer E"};

    log.info("# Begin concert:");
    Flux<String> concertFlux =
        Flux
            .fromArray(singers)
            .delayElements(Duration.ofSeconds(1))
            .share(); // hot sequence로 동작하는 Flux 리턴, 여러 Subscriber가 하나의 원본 Flux를 공유한다.

    concertFlux.subscribe(
        singer -> log.info("# Subscriber1 is watching {}'s song", singer)
    );

    Thread.sleep(2500);

    concertFlux.subscribe(
        singer -> log.info("# Subscriber2 is watching {}'s song", singer)
    );

    Thread.sleep(3000);
  }

  /**
   * HTTP 요청/응답의 Cold Sequence 예제
   */
  private static void example7_3() throws InterruptedException {
    URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
        .host("worldtimeapi.org")
        .port(80)
        .path("/api/timezone/Asia/Seoul")
        .build()
        .encode()
        .toUri();

    Mono<String> mono = getWorldTime(worldTimeUri);
    mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
    Thread.sleep(2000);
    mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));

    Thread.sleep(2000);
  }

  private static Mono<String> getWorldTime(URI worldTimeUri) {
    return WebClient.create()
        .get()
        .uri(worldTimeUri)
        .retrieve()
        .bodyToMono(String.class)
        .map(response -> {
          DocumentContext jsonContext = JsonPath.parse(response);
          String dateTime = jsonContext.read("$.datetime");
          return dateTime;
        });
  }

  /**
   * HTTP 요청/응답의 Hot Sequence 예제
   */
  private static void example7_4() throws InterruptedException {
    URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
        .host("worldtimeapi.org")
        .port(80)
        .path("/api/timezone/Asia/Seoul")
        .build()
        .encode()
        .toUri();

    Mono<String> mono = getWorldTime(worldTimeUri).cache(); // cache() Operator는 Cold Sequence로 동작하는 Mono를 Hot Sequence로 변경해 주고 emit된 데이터를 캐시한 뒤, 구독이 발생할 때마다 캐시된 데이터를 전달한다.
    mono.subscribe(dateTime -> log.info("# dateTime 1: {}", dateTime));
    Thread.sleep(2000);
    mono.subscribe(dateTime -> log.info("# dateTime 2: {}", dateTime));

    Thread.sleep(2000);
  }

  public static void main(String[] args) throws InterruptedException {
    example7_4();
  }
}
