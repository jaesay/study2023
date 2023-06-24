package com.example.webfluxpatterns._05_splitter.service;

import com.example.webfluxpatterns._05_splitter.client.CarClient;
import com.example.webfluxpatterns._05_splitter.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class CarReservationHandler extends ReservationHandler {

  private final CarClient client;

  @Override
  protected ReservationType getType() {
    return ReservationType.CAR;
  }

  @Override
  protected Flux<ReservationItemResponse> reserve(Flux<ReservationItemRequest> flux) {
//    this.client.reserve(flux.map(this::toCarRequest));
    return flux.map(this::toCarRequest) // object to another object
        .transform(this.client::reserve) // flux to another flux
        .map(this::toResponse);
  }

  private CarReservationRequest toCarRequest(ReservationItemRequest request) {
    return CarReservationRequest.create(
        request.getCity(),
        request.getFrom(),
        request.getTo(),
        request.getCategory()
    );
  }

  private ReservationItemResponse toResponse(CarReservationResponse response) {
    return ReservationItemResponse.create(
        response.getReservationId(),
        this.getType(),
        response.getCategory(),
        response.getCity(),
        response.getPickup(),
        response.getDrop(),
        response.getPrice()
    );
  }

}
