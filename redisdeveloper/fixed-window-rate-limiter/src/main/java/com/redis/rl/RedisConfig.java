package com.redis.rl;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.gears.Registration;
import com.redis.lettucemod.api.sync.RedisGearsCommands;
import com.redis.lettucemod.output.ExecutionResults;
import io.lettuce.core.RedisCommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.util.StreamUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
class RedisConfig {

  private final StatefulRedisModulesConnection<String, String> connection;
  private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

  public RedisConfig(StatefulRedisModulesConnection<String, String> connection) {
    this.connection = connection;
  }

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

  @PostConstruct
  public void loadGearsScript() throws IOException {
    String py = StreamUtils.copyToString(new ClassPathResource("scripts/rateLimiter.py").getInputStream(),
        Charset.defaultCharset());
    RedisGearsCommands<String, String> gears = connection.sync();
    List<Registration> registrations = gears.dumpregistrations();

    Optional<String> maybeRegistrationId = getGearsRegistrationIdForTrigger(registrations, "RateLimiter");
    if (maybeRegistrationId.isEmpty()) {
      try {
        ExecutionResults er = gears.pyexecute(py);
        if (er.isOk()) {
          logger.info("RateLimiter.py has been registered");
        } else if (er.isError()) {
          logger.error(String.format("Could not register RateLimiter.py -> %s", Arrays.toString(er.getErrors().toArray())));
        }
      } catch (RedisCommandExecutionException rcee) {
        logger.error(String.format("Could not register RateLimiter.py -> %s", rcee.getMessage()));
      }
    } else {
      logger.info("RateLimiter.py has already been registered");
    }
  }

  private Optional<String> getGearsRegistrationIdForTrigger(List<Registration> registrations, String trigger) {
    return registrations.stream()
        .filter(r -> r.getData().getArgs().get("trigger").equals(trigger))
        .findFirst()
        .map(Registration::getId);
  }
}
