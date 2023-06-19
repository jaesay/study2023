package com.example.webfluxpatterns._03_orchestrator.service;

import com.example.webfluxpatterns._03_orchestrator.client.UserClient;
import com.example.webfluxpatterns._03_orchestrator.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._03_orchestrator.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PaymentOrchestrator extends Orchestrator {

  private final UserClient client;

  @Override
  public Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx) {
    return this.client.deduct(ctx.getPaymentRequest())
        .doOnNext(ctx::setPaymentResponse)
        .thenReturn(ctx);
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Status.SUCCESS == ctx.getPaymentResponse().getStatus();
  }

  @Override
  public Consumer<OrchestrationRequestContext> cancel() {
    return ctx -> Mono.just(ctx)
        .filter(isSuccess())
        .map(OrchestrationRequestContext::getPaymentRequest)
        .flatMap(this.client::refund)
        .subscribe();
  }
}
