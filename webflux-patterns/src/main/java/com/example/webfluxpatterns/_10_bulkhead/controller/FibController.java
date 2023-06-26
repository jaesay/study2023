package com.example.webfluxpatterns._10_bulkhead.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec10")
public class FibController {

  // CPU intensive
  @GetMapping("/fib/{input}")
  public Mono<ResponseEntity<Long>> fib(@PathVariable Long input) {
    return Mono.fromSupplier(() -> findFib(input))
        .map(ResponseEntity::ok);
  }

  private Long findFib(Long input) {
    if (input < 2) {
      return input;
    }
    return findFib(input - 1) + findFib(input - 2);
  }

}
