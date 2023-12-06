package com.example.webclient;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.github.tomakehurst.wiremock.client.WireMock;
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

  @Test
  void getHelloTest() {
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
  void timeoutTest() {
    stubFor(get(urlEqualTo("/v1/hello"))
        .willReturn(aResponse()
            .withFixedDelay(1_000)
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
  void timeoutTest2() {
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
}