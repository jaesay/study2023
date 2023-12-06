package com.example.webclient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWireMock(port = 9090)
class HelloClientTest {

  @Autowired
  HelloClient helloClient;

  @BeforeEach
  void setUp() {
    WireMock.reset();
  }

  @Test
  void getHello() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello())
        .assertNext(res -> assertThat(res).isEqualTo("Hello"))
        .verifyComplete();

    WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/hello")));
  }

  @Test
  void getHello_timeout() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .willReturn(aResponse()
            .withFixedDelay(2_000)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello())
        .assertNext(res -> assertThat(res).isEqualTo("Hi"))
        .verifyComplete();

    WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/hello")));
  }

  @Test
  void getHello2_timeout() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .willReturn(aResponse()
            .withFixedDelay(2_000)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello2())
        .assertNext(res -> assertThat(res).isEqualTo("Hi"))
        .verifyComplete();

    WireMock.verify(1, getRequestedFor(urlEqualTo("/v1/hello")));
  }

  @Test
  @DisplayName("1회를 재시도 포함한 타임아웃 2초 -> 네트워크 지연으로 api 응답이 늦게와도 2초를 기다린다.")
  void getHello3_timeoutRetry() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse()
            .withFixedDelay(1_000)
            .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
        )
        .willSetStateTo("Failed")
    );

    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Failed")
        .willReturn(aResponse()
            .withFixedDelay(500)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello3())
        .assertNext(res -> assertThat(res).isEqualTo("Hello"))
        .verifyComplete();

    WireMock.verify(2, getRequestedFor(urlEqualTo("/v1/hello")));
  }

  @Test
  @DisplayName("타임아웃은 1초, 재시도 1회 -> api 응답지연으로 2초 후에 성공이 와도 1초가 지나면 재시도를 처리한다.")
  void getHello4_timeoutRetry() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse()
            .withFixedDelay(2000)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        )
        .willSetStateTo("Timeout")
    );

    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Timeout")
        .willReturn(aResponse()
            .withFixedDelay(500)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello4())
        .assertNext(res -> assertThat(res).isEqualTo("Hello"))
        .verifyComplete();

    WireMock.verify(2, getRequestedFor(urlEqualTo("/v1/hello")));
  }

  @Test
  @DisplayName("getHello3_timeoutRetry() 와 동일")
  void getHello5_timeoutRetry() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(STARTED)
        .willReturn(aResponse()
            .withFixedDelay(1_000)
            .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
        )
        .willSetStateTo("Failed")
    );

    stubFor(get(urlEqualTo("/v1/hello"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Failed")
        .willReturn(aResponse()
            .withFixedDelay(500)
            .withStatus(HttpStatus.OK.value())
            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
            .withBodyFile("hello.txt")
        ));

    StepVerifier.create(helloClient.getHello5())
        .assertNext(res -> assertThat(res).isEqualTo("Hello"))
        .verifyComplete();

    WireMock.verify(2, getRequestedFor(urlEqualTo("/v1/hello")));
  }
}