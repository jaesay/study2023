package com.example.webfluxpatterns._02_scattergather.service;

import com.example.webfluxpatterns._02_scattergather.client.DeltaClient;
import com.example.webfluxpatterns._02_scattergather.client.FrontierClient;
import com.example.webfluxpatterns._02_scattergather.client.JetBlueClient;
import com.example.webfluxpatterns._02_scattergather.dto.FlightResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FlightSearchService {

  private final DeltaClient deltaClient;
  private final FrontierClient frontierClient;
  private final JetBlueClient jetBlueClient;

  public Flux<FlightResult> getFlights(String from, String to) {
    return Flux.merge(
            this.deltaClient.getFlights(from, to),
            this.frontierClient.getFlights(from, to),
            this.jetBlueClient.getFlights(from, to)
        )
        .log()
        .take(Duration.ofSeconds(3));
  }
}
