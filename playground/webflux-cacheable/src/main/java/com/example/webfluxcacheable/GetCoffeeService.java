package com.example.webfluxcacheable;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GetCoffeeService {

  public Mono<Coffee> getCoffee(long coffeeId) {
    return Mono.just(new Coffee(coffeeId, "name"));
  }
}
