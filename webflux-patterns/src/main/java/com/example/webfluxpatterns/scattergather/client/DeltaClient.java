package com.example.webfluxpatterns.scattergather.client;

import com.example.webfluxpatterns.scattergather.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DeltaClient {

  private final WebClient client;

  public DeltaClient(@Value("${client.scatter-gather.delta-url}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to) {
    return this.client
        .get()
        .uri("{from}/{to}", from, to)
        .retrieve()
        .bodyToFlux(FlightResult.class)
        .log()
        .onErrorResume(ex -> Mono.empty());
  }
}
