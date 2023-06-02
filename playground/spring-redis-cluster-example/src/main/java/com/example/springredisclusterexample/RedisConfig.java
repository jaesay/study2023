package com.example.springredisclusterexample;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@RequiredArgsConstructor
@Configuration
public class RedisConfig {
  private final RedisProperties redisProperties;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    var clusterConfig = new RedisClusterConfiguration(redisProperties.getCluster().getNodes());
    clusterConfig.setPassword(redisProperties.getPassword());

    var topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
        .enableAllAdaptiveRefreshTriggers()
        .build();

    var clientOptions = ClusterClientOptions.builder()
        .topologyRefreshOptions(topologyRefreshOptions)
        .build();

    var clientConfig = LettuceClientConfiguration.builder()
        .clientOptions(clientOptions)
        .build();

    return new LettuceConnectionFactory(clusterConfig, clientConfig);
  }
}
