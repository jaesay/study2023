package com.example.webfluxpatterns._05_splitter.service;

import com.example.webfluxpatterns._05_splitter.dto.ReservationItemRequest;
import com.example.webfluxpatterns._05_splitter.dto.ReservationItemResponse;
import com.example.webfluxpatterns._05_splitter.dto.ReservationType;
import reactor.core.publisher.Flux;

public abstract class ReservationHandler {

  protected abstract ReservationType getType();
  protected abstract Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux);

}
