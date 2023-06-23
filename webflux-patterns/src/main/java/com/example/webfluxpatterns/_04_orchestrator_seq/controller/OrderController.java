package com.example.webfluxpatterns._04_orchestrator_seq.controller;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrderRequest;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrderResponse;
import com.example.webfluxpatterns._04_orchestrator_seq.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("sec04")
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
