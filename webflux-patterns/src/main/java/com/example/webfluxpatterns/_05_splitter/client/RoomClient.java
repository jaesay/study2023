package com.example.webfluxpatterns._05_splitter.client;

import com.example.webfluxpatterns._05_splitter.dto.RoomReservationRequest;
import com.example.webfluxpatterns._05_splitter.dto.RoomReservationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class RoomClient {

  private final WebClient client;

  public RoomClient(@Value("${sec05.room.service}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<RoomReservationResponse> reserve(Flux<RoomReservationRequest> flux) {
    return this.client
        .post()
        .body(flux, RoomReservationRequest.class)
        .retrieve()
        .bodyToFlux(RoomReservationResponse.class)
        .onErrorResume(ex -> Mono.empty());
  }
}
