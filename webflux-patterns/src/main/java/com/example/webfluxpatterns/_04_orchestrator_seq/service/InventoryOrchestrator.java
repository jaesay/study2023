package com.example.webfluxpatterns._04_orchestrator_seq.service;

import com.example.webfluxpatterns._04_orchestrator_seq.client.InventoryClient;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
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
