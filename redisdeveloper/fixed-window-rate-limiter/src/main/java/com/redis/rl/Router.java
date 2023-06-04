package com.redis.rl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
class Router {

  private final ReactiveRedisTemplate<String, Long> redisTemplate;
  private final RedisScript<Boolean> script;
  private final Long maxRequestPerMinute;

  public Router(ReactiveRedisTemplate<String, Long> redisTemplate,
                RedisScript<Boolean> script,
                @Value("${MAX_REQUESTS_PER_MINUTE}") Long maxRequestPerMinute) {

    this.redisTemplate = redisTemplate;
    this.script = script;
    this.maxRequestPerMinute = maxRequestPerMinute;
  }

  @Bean
  RouterFunction<ServerResponse> routes() {
    return route()
        .GET("/api/ping", r -> ok()
            .contentType(TEXT_PLAIN)
            .body(BodyInserters.fromValue("PONG"))
        )
//        .filter(new RateLimiterHandlerFilterFunction(redisTemplate))
        .filter(new LuaRateLimiterHandlerFilterFunction(redisTemplate, script, maxRequestPerMinute))
        .build();
  }
}
