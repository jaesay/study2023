package com.example.webfluxcacheable;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class CoffeeRouter {

  @Bean
  public RouterFunction<?> getCoffeeRouter(GetCoffeeHandler handler) {
    return route()
        .GET("/coffees/{coffeeId}", handler::getCoffee)
        .build();
  }
}
