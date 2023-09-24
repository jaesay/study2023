package com.example.webfluxcacheable;

import static java.util.stream.Collectors.toMap;

import com.example.webfluxcacheable.conifg.CaffeineProperties;
import com.example.webfluxcacheable.conifg.CaffeineProperties.CacheProperties;
import java.time.Duration;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GetCoffeeService {

  private final CacheManager cacheManager;
  private final Map<String, CacheProperties> propsMap;

  public GetCoffeeService(CacheManager cacheManager, CaffeineProperties props) {
    this.cacheManager = cacheManager;
    this.propsMap = props.getCaches().stream()
        .collect(toMap(CacheProperties::getName, Function.identity()));
  }

  @Cacheable(cacheNames = "coffeeCache", key = "#coffeeId")
  public Mono<Coffee> getCoffee(long coffeeId) {
    return Mono.defer(() -> coffee(coffeeId)).cache();
  }

  /**
   * 이렇게 cache() operator에 ttl을 주면 카페인 캐시 기본 만료 정책에 따라 다음 조회까지 signal의 결괏값 (Coffee instance) 를 캐싱하는 것은 제외하고 Mono의 reference 만 가진다.
   * 하지만 maintenance(BoundedLocalCache) 작업(e.g. 특정/새로운 엔트리 조회 등)을 하기 때문에 대부분의 경우 이 정도로 메모리 최적화할 필요는 없을 것 같다.
   */
  @Cacheable(cacheNames = "coffeeCacheV2", key = "#coffeeId")
  public Mono<Coffee> getCoffeeV2(long coffeeId) {
    CacheProperties coffeeCache = propsMap.get("coffeeCacheV2");
    return Mono.defer(() -> coffee(coffeeId)).cache(Duration.of(
        coffeeCache.getExpiryDurationAmount(),
        coffeeCache.getExpiryDurationTimeUnit().toChronoUnit()));
  }

  private Mono<Coffee> coffee(long coffeeId) {
    log.info("GetCoffeeService#coffee: " + coffeeId);
    return Mono.just(new Coffee(coffeeId, "name"));
  }
}
