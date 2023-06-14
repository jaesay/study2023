package com.example.webfluxpatterns.aggregator.controller;

import com.example.webfluxpatterns.aggregator.dto.ProductAggregate;
import com.example.webfluxpatterns.aggregator.service.ProductAggregateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/aggregators")
@RequiredArgsConstructor
public class ProductAggregateController {

  private final ProductAggregateService service;

  @GetMapping("/products/{id}")
  public Mono<ResponseEntity<ProductAggregate>> getProductAggregate(@PathVariable("id") long productId) {
    return service.aggregate(productId)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }
}
