package com.redis.rl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
class Router {

  private final ReactiveRedisTemplate<String, Long> redisTemplate;

  public Router(ReactiveRedisTemplate<String, Long> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
        .GET("/api/ping", r -> ok()
            .contentType(TEXT_PLAIN)
            .body(BodyInserters.fromValue("PONG"))
        )
        .filter(new RateLimiterHandlerFilterFunction(redisTemplate))
        .build();
  }
}
