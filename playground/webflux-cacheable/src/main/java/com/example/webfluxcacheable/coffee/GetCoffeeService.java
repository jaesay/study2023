package com.example.webfluxcacheable.coffee;

import static java.util.stream.Collectors.toMap;

import com.example.webfluxcacheable.config.CaffeineProperties;
import com.example.webfluxcacheable.config.CaffeineProperties.CacheProperties;
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
   * 카페인 캐시를 포함한 대부분의 캐시 구현체는 데이터가 만료되면 바로 만료된 데이터를 제거하지 않는다. 따라서 cache()에 ttl을 안주면 Mono와 캐시된 시그널을 캐시하고 있게 된다.
   * cache() operator에 ttl을 주면 Mono는 스프링 캐시에 만료 정책에 의해 관리되고 Mono 캐시된 시그널 결괏값(e.g. Coffee instance)은 cache() ttl이 지나면 사라진다.
   * 하지만 캐시 구현체는 설정한 정책에 맞게 내부적으로 메모리를 잘 관리하기 때문에 대부분의 경우 이 정도의 최적화는 안해도 될 것 같다(BoundedLocalCache).
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
