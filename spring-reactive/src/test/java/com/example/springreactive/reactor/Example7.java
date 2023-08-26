package com.example.springreactive.reactor;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Example7 {

  @Test
  @DisplayName("Cold Sequence 예제")
  void example7_1() throws InterruptedException {
    Flux<String> coldFlux = Flux
        .fromIterable(List.of("KOREA", "JAPAN", "CHINESE"))
        .map(String::toLowerCase);

    coldFlux.subscribe(country -> System.out.println(
        "[" + Thread.currentThread().getName() + "] Subscriber1: " + country));
    System.out.println("----------------------------------------------------------------------");
    Thread.sleep(2000L);
    // 구독이 발생할 때마다 emit된 데이터를 처음부터 다시 전달한다.
    coldFlux.subscribe(country -> System.out.println(
        "[" + Thread.currentThread().getName() + "] Subscriber2: " + country));
  }

  @Test
  @DisplayName("Hot Sequence 예제")
  void example7_2() throws InterruptedException {
    String[] singers = {"Singer A", "Singer B", "Singer C", "Singer D", "Singer E"};

    System.out.println("# Begin concert:");
    Flux<String> concertFlux = Flux
        .fromArray(singers)
        .delayElements(Duration.ofSeconds(1))
        .share(); // hot sequence로 동작하는 Flux 리턴, 여러 Subscriber가 하나의 원본 Flux를 공유한다.

    concertFlux.subscribe(
        singer -> System.out.printf("[%s] Subscriber1 is watching %s's song%n",
            Thread.currentThread().getName(), singer)
    );

    Thread.sleep(2500);

    concertFlux.subscribe(
        singer -> System.out.printf("[%s] Subscriber2 is watching %s's song%n",
            Thread.currentThread().getName(), singer)
    );

    Thread.sleep(3000);
  }

  @Test
  @DisplayName("HTTP 요청/응답의 Cold Sequence 예제")
  void example7_3() throws InterruptedException {
    URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
        .host("worldtimeapi.org")
        .port(80)
        .path("/api/timezone/Asia/Seoul")
        .build()
        .encode()
        .toUri();

    Mono<String> mono = getWorldTime(worldTimeUri);
    mono.subscribe(dateTime -> System.out.printf("[%s] dateTime 1: %s%n",
        Thread.currentThread().getName(), dateTime));
    Thread.sleep(2000);
    mono.subscribe(dateTime -> System.out.printf("[%s] dateTime 2: %s%n",
        Thread.currentThread().getName(), dateTime));

    Thread.sleep(2000);
  }

  @Test
  @DisplayName("HTTP 요청/응답의 Hot Sequence 예제")
  void example7_4() throws InterruptedException {
    URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
        .host("worldtimeapi.org")
        .port(80)
        .path("/api/timezone/Asia/Seoul")
        .build()
        .encode()
        .toUri();

    Mono<String> mono = getWorldTime(worldTimeUri)
        .cache(); // cache() Operator는 Cold Sequence로 동작하는 Mono를 Hot Sequence로 변경해 주고 emit된 데이터를 캐시한 뒤, 구독이 발생할 때마다 캐시된 데이터를 전달한다.
    mono.subscribe(dateTime -> System.out.printf("[%s] dateTime 1: %s%n",
        Thread.currentThread().getName(), dateTime));
    Thread.sleep(2000);
    mono.subscribe(dateTime -> System.out.printf("[%s] dateTime 2: %s%n",
        Thread.currentThread().getName(), dateTime));

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
}
