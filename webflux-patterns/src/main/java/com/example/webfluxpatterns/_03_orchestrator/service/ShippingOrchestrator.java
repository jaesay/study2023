package com.example.webfluxpatterns._03_orchestrator.service;

import com.example.webfluxpatterns._03_orchestrator.client.ShippingClient;
import com.example.webfluxpatterns._03_orchestrator.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._03_orchestrator.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ShippingOrchestrator extends Orchestrator {

  private final ShippingClient client;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    return this.client.schedule(ctx.getShippingRequest())
        .doOnNext(ctx::setShippingResponse)
        .thenReturn(ctx);
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Status.SUCCESS == ctx.getShippingResponse().getStatus();
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx)
        .filter(isSuccess())
        .map(OrchestrationRequestContext::getShippingRequest)
        .flatMap(this.client::cancel)
        .subscribe();
  }
}
