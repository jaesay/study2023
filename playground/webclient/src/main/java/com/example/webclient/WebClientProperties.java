package com.example.webclient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "webclient")
public record WebClientProperties(int connectionTimeout, int readTimeout, Map<String, Pool> pools) {

  public record Pool(int maxConnections,
                     int pendingAcquireMaxCount,
                     Duration maxIdleTime,
                     Duration maxLifeTime,
                     Duration evictInBackground,
                     boolean metricsEnabled) {

  }
}
