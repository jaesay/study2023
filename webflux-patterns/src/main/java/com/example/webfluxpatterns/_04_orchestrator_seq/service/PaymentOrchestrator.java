package com.example.webfluxpatterns._04_orchestrator_seq.service;

import com.example.webfluxpatterns._04_orchestrator_seq.client.UserClient;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
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
        .thenReturn(ctx)
        .handle(this.statusHandler());
  }

  @Override
  public Predicate<OrchestrationRequestContext> isSuccess() {
    return ctx -> Objects.nonNull(ctx.getPaymentResponse()) && Status.SUCCESS == ctx.getPaymentResponse().getStatus();
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
