package com.redis.rl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
class RedisConfig {

  @Bean
  ReactiveRedisTemplate<String, Long> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
    JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
    StringRedisSerializer stringRedisSerializer = StringRedisSerializer.UTF_8;
    GenericToStringSerializer<Long> longToStringSerializer = new GenericToStringSerializer<>(Long.class);

    ReactiveRedisTemplate<String, Long> template = new ReactiveRedisTemplate<>(factory,
        RedisSerializationContext.<String, Long>newSerializationContext(jdkSerializationRedisSerializer)
            .key(stringRedisSerializer).value(longToStringSerializer).build());

    return template;
  }

  @Bean
  RedisScript<Boolean> script() {
    return RedisScript.of(new ClassPathResource("scripts/rateLimiter.lua"), Boolean.class);
  }
}
