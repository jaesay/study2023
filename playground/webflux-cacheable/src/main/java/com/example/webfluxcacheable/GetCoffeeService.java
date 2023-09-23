package com.example.webfluxcacheable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetCoffeeService {

  private final CacheManager cacheManager;

  @Cacheable(cacheNames = "coffeeCache", key = "#coffeeId")
  public Mono<Coffee> getCoffee(long coffeeId) {
    return Mono.defer(() -> coffee(coffeeId)).cache();
  }

  public Mono<Coffee> coffee(long coffeeId) {
    log.info("GetCoffeeService#coffee: " + coffeeId);
    return Mono.just(new Coffee(coffeeId, "name"));
  }
}
