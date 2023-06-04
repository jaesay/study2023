package com.redis.rl;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisCallback;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class RateLimiterHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  private final ReactiveRedisTemplate<String, Long> redisTemplate;

  private static final long MAX_REQUESTS_PER_MINUTE = 20L;

  public RateLimiterHandlerFilterFunction(ReactiveRedisTemplate<String, Long> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
    int currentMinute = LocalTime.now().getMinute();
    String key = String.format("rl_%s:%s", requestAddress(request.remoteAddress()), currentMinute);
    System.out.println(">>>> key " + key);

    return redisTemplate
        .opsForValue().get(key)
        .flatMap(
            value -> value >= MAX_REQUESTS_PER_MINUTE ?
                ServerResponse.status(TOO_MANY_REQUESTS).build() :
                incrAndExpireKey(key, request, next)
        ).switchIfEmpty(incrAndExpireKey(key, request, next));
  }

  private String requestAddress(Optional<InetSocketAddress> maybeAddress) {
    return maybeAddress.isPresent() ? maybeAddress.get().getHostName() : "";
  }

  private Mono<ServerResponse> incrAndExpireKey(String key, ServerRequest request, HandlerFunction<ServerResponse> next) {
    return redisTemplate.execute((ReactiveRedisCallback<List<Object>>) connection -> {
      ByteBuffer bbKey = ByteBuffer.wrap(key.getBytes());

      return Mono.zip(
          connection.numberCommands().incr(bbKey),
          connection.keyCommands().expire(bbKey, Duration.ofSeconds(59L))
      ).then(Mono.empty());

    }).then(next.handle(request));
  }
}
