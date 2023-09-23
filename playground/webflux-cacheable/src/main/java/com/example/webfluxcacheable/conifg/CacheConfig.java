package com.example.webfluxcacheable.conifg;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CacheConfig {

  private final CaffeineProperties props;

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    List<CaffeineCache> caches = props.getCaches()
        .stream()
        .map(cache ->
            new CaffeineCache(
                cache.getName(),
                Caffeine.newBuilder()
                    .recordStats()
                    .expireAfterWrite(cache.getExpiryDurationAmount(), cache.getExpiryDurationTimeUnit())
                    .build())
        )
        .collect(Collectors.toList());

    cacheManager.setCaches(caches);
    return cacheManager;
  }
}
