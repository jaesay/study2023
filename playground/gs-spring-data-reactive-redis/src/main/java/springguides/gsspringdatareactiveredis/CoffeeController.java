package springguides.gsspringdatareactiveredis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class CoffeeController {
  private final ReactiveRedisOperations<String, Coffee> coffeeOps;

  @GetMapping("/coffees")
  public Flux<Coffee> all() {
    return coffeeOps.keys("*")
        .flatMap(coffeeOps.opsForValue()::get);
  }
}
