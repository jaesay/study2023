package com.example.redisdistributedlock;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;

@Configuration
@RequiredArgsConstructor
public class LockConfig {

  private final RedisProperties redisProperties;

  @Bean
  public RedisConnectionFactory lockRedisConnectionFactory() {
    RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(redisProperties.getHost(), redisProperties.getPort());

    return new LettuceConnectionFactory(configuration);
  }

  @Bean(name = "lockRedisTemplate")
  public RedisTemplate<LockKey, Long> lockRedisTemplate() {
    RedisTemplate<LockKey, Long> lockRedisTemplate = new RedisTemplate<>();
    lockRedisTemplate.setConnectionFactory(lockRedisConnectionFactory());
    lockRedisTemplate.setKeySerializer(new LockKeySerializer());
    lockRedisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));
    return lockRedisTemplate;
  }
}
