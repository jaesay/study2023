package com.example.webfluxpatterns._05_splitter.controller;

import com.example.webfluxpatterns._05_splitter.dto.ReservationItemRequest;
import com.example.webfluxpatterns._05_splitter.dto.ReservationResponse;
import com.example.webfluxpatterns._05_splitter.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec05")
@RequiredArgsConstructor
public class ReservationController {

  private final ReservationService service;

  @PostMapping("reservations")
  public Mono<ReservationResponse> reserve(@RequestBody Flux<ReservationItemRequest> flux) {
    return this.service.reserve(flux);
  }

}
