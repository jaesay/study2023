package com.example.webfluxcacheable.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "caffeine")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class CaffeineProperties {

  private final List<CacheProperties> caches = new ArrayList<>();

  @RequiredArgsConstructor
  @Getter
  public static class CacheProperties {

    private final String name;
    private final TimeUnit expiryDurationTimeUnit;
    private final long expiryDurationAmount;
  }
}
