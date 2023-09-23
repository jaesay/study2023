package com.example.webfluxcacheable;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GetCoffeeHandler {

  private final GetCoffeeService service;

  public Mono<ServerResponse> getCoffee(ServerRequest request) {
    String coffeeId = request.pathVariable("coffeeId");
    return service.getCoffee(Long.parseLong(coffeeId))
        .flatMap(coffee ->
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(coffee)
        );
  }
}
