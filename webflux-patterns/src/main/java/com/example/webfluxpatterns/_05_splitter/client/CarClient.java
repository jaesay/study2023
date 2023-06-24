package com.example.webfluxpatterns._05_splitter.client;

import com.example.webfluxpatterns._05_splitter.dto.CarReservationRequest;
import com.example.webfluxpatterns._05_splitter.dto.CarReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CarClient {

  private final WebClient client;

  public CarClient(@Value("${sec05.car.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  /**
   * Flux로 요청을 보내고 Flux로 리턴을 받지만 HTTP 한계와 External Service의 구현에 인해 실제로는 List로 보낸 후 응답을 받고 Flux로 변환하는 방식으로 이루어진다.
   *
   * @param flux
   * @return
   */
  public Flux<CarReservationResponse> reserve(Flux<CarReservationRequest> flux) {
    return this.client
        .post()
        .body(flux, CarReservationRequest.class)
        .retrieve()
        .bodyToFlux(CarReservationResponse.class)
        .onErrorResume(ex -> Mono.empty());
  }

}
