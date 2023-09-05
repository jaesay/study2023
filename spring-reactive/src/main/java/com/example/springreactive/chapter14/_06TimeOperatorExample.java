package com.example.springreactive.chapter14;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

/**
 * CHAPTER 14.7. Sequence의 동작 시간 측정을 위한 Operator
 */
@Slf4j
public class _06TimeOperatorExample {

  /**
   * elapsed 예제 1 - emit된 데이터 사이의 경과된 시간을 측정한다. - emit된 첫번째 데이터는 onSubscribe Signal과 첫번째 데이터 사이의 시간을
   * 기준으로 측정한다. - 측정된 시간 단위는 milliseconds이다.
   */
  private static void example14_51() throws InterruptedException {
    Flux
        .range(1, 5)
        .delayElements(Duration.ofSeconds(1))
        .elapsed()
        .subscribe(data -> log.info("# onNext: {}, time: {}",
            data.getT2(), data.getT1()));

    Thread.sleep(6000);
  }

  /**
   * elapsed 예제 2
   */
  private static void example14_52() {
    URI worldTimeUri = UriComponentsBuilder.newInstance().scheme("http")
        .host("worldtimeapi.org")
        .port(80)
        .path("/api/timezone/Asia/Seoul")
        .build()
        .encode()
        .toUri();

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    Mono.defer(() -> Mono.just(
                restTemplate
                    .exchange(worldTimeUri,
                        HttpMethod.GET,
                        new HttpEntity<String>(headers),
                        String.class)
            )
        )
        .repeat(4) // 총 5회 = 최초 구독 시 1회 + repeat Operator() 파라미터 4회
        .elapsed()
        .map(response -> {
          DocumentContext jsonContext =
              JsonPath.parse(response.getT2().getBody());
          String dateTime = jsonContext.read("$.datetime");
          return Tuples.of(dateTime, response.getT1());
        })
        .subscribe(
            data -> log.info("now: {}, elapsed time: {}", data.getT1(), data.getT2()),
            error -> log.error("# onError:", error),
            () -> log.info("# onComplete")
        );
  }

  public static void main(String[] args) throws InterruptedException {
    example14_52();
  }
}
