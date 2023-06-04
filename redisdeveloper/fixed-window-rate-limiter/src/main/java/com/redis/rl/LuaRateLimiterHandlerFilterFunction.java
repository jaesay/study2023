package com.redis.rl;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class LuaRateLimiterHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  private final ReactiveRedisTemplate<String, Long> redisTemplate;
  private final RedisScript<Boolean> script;
  private final Long maxRequestPerMinute;

  public LuaRateLimiterHandlerFilterFunction(ReactiveRedisTemplate<String, Long> redisTemplate, RedisScript<Boolean> script, Long maxRequestPerMinute) {
    this.redisTemplate = redisTemplate;
    this.script = script;
    this.maxRequestPerMinute = maxRequestPerMinute;
  }

  @Override
  public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
    int currentMinute = LocalTime.now().getMinute();
    String key = String.format("rl_%s:%s", requestAddress(request.remoteAddress()), currentMinute);

    return redisTemplate
        .execute(script, List.of(key), List.of(maxRequestPerMinute, 59))
        .single(false)
        .flatMap(value -> value ?
            ServerResponse.status(TOO_MANY_REQUESTS).build() :
            next.handle(request));
  }

  private String requestAddress(Optional<InetSocketAddress> maybeAddress) {
    return maybeAddress.isPresent() ? maybeAddress.get().getHostName() : "";
  }
}
