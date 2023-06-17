package com.example.webfluxpatterns._02_scattergather.client;

import com.example.webfluxpatterns._02_scattergather.dto.FlightResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FrontierClient {

  private final WebClient client;

  public FrontierClient(@Value("${client.scatter-gather.frontier-url}") String baseUrl) {
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Flux<FlightResult> getFlights(String from, String to){
    return this.client
        .post()
        .bodyValue(FrontierRequest.create(from, to))
        .retrieve()
        .bodyToFlux(FlightResult.class)
        .log()
        .onErrorResume(ex -> Mono.empty());
  }

  @Data
  @AllArgsConstructor(staticName = "create")
  private static class FrontierRequest{
    private String from;
    private String to;
  }
}
