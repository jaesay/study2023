package com.example.springreactive.reactor;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Example6 {

  @Test
  @DisplayName("Mono 기본 개념 예제 - 1개의 데이터를 생성해서 emit한다.")
  void example6_1() {
    Mono.just("Hello Reactor")
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Mono 기본 개념 예제 - 원본 데이터의 emit 없이 onComplete signal 만 emit 한다.")
  void example6_2() {
    Mono
        .empty() // emit할 데이터가 없는 것으로 간주하여 곧바로 onComplete Signal을 전송한다. 어떤 특정 작업을 통해 데이터를 전달받을 필요는 없지만 작업이 끝났음을 알리고 이에 따른 후처리를 하고 싶을 때 사용할 수 있다.
        .doOnNext(none -> System.out.println("Operator 실행 안됨"))
        .subscribe(
            none -> System.out.println("Publisher가 onNext Signal을 전송하면 실행"),
            error -> System.out.println("Publisher가 onError Signal을 전송하면 실행"),
            () -> System.out.println("Publisher가 onComplete Signal을 전송하면 실행")
        );
  }

  @Test
  @DisplayName("Mono 활용 예제 - worldtimeapi.org Open API를 이용해서 서울의 현재 시간을 조회한다.")
  void example6_3() {
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

    // Mono를 사용하여 HTTP 요청/응답을 처리하면 요청과 응답을 하나의 Operator 체인으로 깔끔하게 처리할 수 있다.
    Mono.just(
            restTemplate
                .exchange(worldTimeUri,
                    HttpMethod.GET,
                    new HttpEntity<String>(headers),
                    String.class)
        )
        .map(response -> {
          DocumentContext jsonContext = JsonPath.parse(response.getBody());
          String dateTime = jsonContext.read("$.datetime");
          return dateTime;
        })
        .subscribe(
            data -> System.out.println("# emitted data: " + data),
            System.out::println,
            () -> System.out.println("# emitted onComplete signal")
        );
  }

  @Test
  @DisplayName("Flux 기본 예제")
  void example6_4() {
    Flux.just(3, 6, 13)
        .map(num -> num % 2)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("Flux 에서의 Operator 체인 사용 예제")
  void example6_5() {
    Flux.fromArray(new Integer[]{3, 6, 7, 9})
        .filter(num -> num > 6)
        .map(num -> num * 2)
        .subscribe(System.out::println);
  }

  @Test
  @DisplayName("2개의 Mono를 연결해서 Flux로 변환하는 예제")
  void example6_6() {
    Flux<String> flux = Mono
        .justOrEmpty("Steve") // just() Operator의 경우 파라미터의 값으로 null을 허용하지 않지만 justOrEmpty()는 null을 허용한다. null이 전달되면 내부적으로 empty() Operator를 호출하도록 구현되어 있다.
        .concatWith(Mono.justOrEmpty("Jobs")); // concatWith() Operator는 concatWith()를 호출하는 Publisher와 concatWith()의 파라미터로 전달되는 Publisher가 각각 emit하는 데이터들을 하나로 연결해서 새로운 Publisher의 데이터 소스로 만들어 준다.

    flux.subscribe(System.out::println);
  }

  @Test
  @DisplayName("여러개의 Flux를 연결해서 하나의 Flux로 결합하는 예제")
  void example6_7() {
    // concatWith()의 경우 두 개의 데이터 소스만 연결할 수 있지만, concat()은 여러 개의 데이터 소스를 원하는 만큼 연결할 수 있다.
    Mono<List<String>> listMono = Flux.concat(
            Flux.just("Mercury", "Venus", "Earth"),
            Flux.just("Mars", "Jupiter", "Saturn"),
            Flux.just("Uranus", "Neptune", "Pluto"))
        .collectList(); // collectList() Operator는 Upstream Publisher에서 emit하는 데이터를 모아서 List의 원소로 포함시킨 새로운 데이터 소스를 만들어 준다.

    listMono
        .subscribe(System.out::println);
  }
}
