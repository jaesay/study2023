package com.example.webfluxpatterns.scattergather.client;

import com.example.webfluxpatterns.scattergather.dto.FlightResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class JetBlueClient {

  private static final String JETBLUE = "JETBLUE";
  private final WebClient client;

  public JetBlueClient(@Value("${client.scatter-gather.jetblue-url}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to){
    return this.client
        .get()
        .uri("{from}/{to}", from, to)
        .retrieve()
        .bodyToFlux(FlightResult.class)
        .log()
        .doOnNext(fr -> this.normalizeResponse(fr, from, to))
        .onErrorResume(ex -> Mono.empty());
  }

  private void normalizeResponse(FlightResult result, String from, String to){
    result.setFrom(from);
    result.setTo(to);
    result.setAirline(JETBLUE);
  }
}
