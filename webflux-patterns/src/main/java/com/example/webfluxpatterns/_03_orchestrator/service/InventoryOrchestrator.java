package com.example.webfluxpatterns._03_orchestrator.service;

import com.example.webfluxpatterns._03_orchestrator.client.InventoryClient;
import com.example.webfluxpatterns._03_orchestrator.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._03_orchestrator.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class InventoryOrchestrator extends Orchestrator {

  private final InventoryClient client;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    return this.client.deduct(ctx.getInventoryRequest())
        .doOnNext(ctx::setInventoryResponse)
        .thenReturn(ctx);
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Status.SUCCESS == ctx.getInventoryResponse().getStatus();
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx)
        .filter(isSuccess())
        .map(OrchestrationRequestContext::getInventoryRequest)
        .flatMap(this.client::restore)
        .subscribe();
  }
}
