package com.example.springdatareactiveredis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveKeyCommands;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

  @Bean
  public ReactiveRedisTemplate<String, Employee> reactiveRedisTemplate(
      ReactiveRedisConnectionFactory factory) {

    Jackson2JsonRedisSerializer<Employee> serializer = new Jackson2JsonRedisSerializer<>(
        Employee.class);
    RedisSerializationContextBuilder<String, Employee> builder = RedisSerializationContext
        .newSerializationContext(new StringRedisSerializer());
    RedisSerializationContext<String, Employee> context = builder.value(serializer)
        .build();
    return new ReactiveRedisTemplate<>(factory, context);
  }

  @Bean
  public ReactiveKeyCommands keyCommands(
      final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

    return reactiveRedisConnectionFactory.getReactiveConnection()
        .keyCommands();
  }

  @Bean
  public ReactiveStringCommands stringCommands(
      final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

    return reactiveRedisConnectionFactory.getReactiveConnection()
        .stringCommands();
  }

}
