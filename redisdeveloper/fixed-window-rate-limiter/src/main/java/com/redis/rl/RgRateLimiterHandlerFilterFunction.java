package com.redis.rl;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.sync.RedisGearsCommands;
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

public class RgRateLimiterHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

  private final StatefulRedisModulesConnection<String, String> connection;
  private final Long maxRequestPerMinute;

  public RgRateLimiterHandlerFilterFunction(StatefulRedisModulesConnection<String, String> connection, Long maxRequestPerMinute) {
    this.connection = connection;
    this.maxRequestPerMinute = maxRequestPerMinute;
  }

  @Override
  public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {
    int currentMinute = LocalTime.now().getMinute();
    String key = String.format("rl_%s:%s", requestAddress(request.remoteAddress()), currentMinute);

    RedisGearsCommands<String, String> gears = connection.sync();

    List<Object> results = gears.trigger("RateLimiter", key, Long.toString(maxRequestPerMinute), "59");
    if (!results.isEmpty() && !Boolean.parseBoolean((String) results.get(0))) {
      return next.handle(request);
    } else {
      return ServerResponse.status(TOO_MANY_REQUESTS).build();
    }
  }

  private String requestAddress(Optional<InetSocketAddress> maybeAddress) {
    return maybeAddress.isPresent() ? maybeAddress.get().getHostName() : "";
  }
}
