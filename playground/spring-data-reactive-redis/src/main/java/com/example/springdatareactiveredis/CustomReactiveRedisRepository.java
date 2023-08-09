package com.example.springdatareactiveredis;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import reactor.core.publisher.Mono;

public abstract class CustomReactiveRedisRepository<T, ID> implements CustomReactiveCrudRepository<T, ID> {

  private final ReactiveValueOperations<String, T> reactiveValueOps;
  private final String redisKey;

  public CustomReactiveRedisRepository(ReactiveRedisConnectionFactory factory, Class<?> valueClass, String redisKey) {
    var objectMapper = Jackson2ObjectMapperBuilder.json().build();
    var keySerializer = new StringRedisSerializer();
    var valueSerializer = new Jackson2JsonRedisSerializer<>(valueClass);
    valueSerializer.setObjectMapper(objectMapper);

    var ctx = RedisSerializationContext
        .<String, T>newSerializationContext()
        .key(keySerializer)
        .hashKey(keySerializer)
        .value((RedisSerializer<T>) valueSerializer)
        .hashValue(valueSerializer)
        .build();

    ReactiveRedisTemplate<String, T> template = new ReactiveRedisTemplate<>(factory, ctx);
    this.reactiveValueOps = template.opsForValue();
    this.redisKey = redisKey;
  }

  @Override
  public Mono<T> findById(ID id) {
    return reactiveValueOps.get(String.format("%s:%s", redisKey, id));
  }

  @Override
  public Mono<List<T>> findAllById(Collection<ID> ids) {
    return reactiveValueOps.multiGet(ids.stream()
        .map(id -> String.format("%s:%s", redisKey, id))
        .collect(Collectors.toList()));
  }
}
