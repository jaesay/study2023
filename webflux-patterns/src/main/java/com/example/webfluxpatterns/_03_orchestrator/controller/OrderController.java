package com.example.webfluxpatterns._03_orchestrator.controller;

import com.example.webfluxpatterns._03_orchestrator.dto.OrderRequest;
import com.example.webfluxpatterns._03_orchestrator.dto.OrderResponse;
import com.example.webfluxpatterns._03_orchestrator.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec03")
@RequiredArgsConstructor
public class OrderController {

  private final OrchestratorService service;

  @PostMapping("/order")
  public Mono<ResponseEntity<OrderResponse>> placeOrder(@RequestBody Mono<OrderRequest> mono) {
    return this.service.placeOrder(mono)
        .map(ResponseEntity::ok)
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

}
