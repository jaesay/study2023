package com.example.webfluxpatterns.scattergather.controller;

import com.example.webfluxpatterns.scattergather.dto.FlightResult;
import com.example.webfluxpatterns.scattergather.service.FlightSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/scatter-gather")
@RequiredArgsConstructor
public class FlightsController {

  private final FlightSearchService service;

  @GetMapping(value = "/flights/{from}/{to}", produces = TEXT_EVENT_STREAM_VALUE)
  public Flux<FlightResult> getFlights(@PathVariable String from, @PathVariable String to) {
    return this.service.getFlights(from, to);
  }

}
