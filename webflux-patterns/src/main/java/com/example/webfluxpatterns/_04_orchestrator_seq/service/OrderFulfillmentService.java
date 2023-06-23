package com.example.webfluxpatterns._04_orchestrator_seq.service;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentService {

  private final List<Orchestrator> orchestrators;

  public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx) {
    var list = orchestrators.stream()
        .map(o -> o.create(ctx))
        .collect(Collectors.toList());

    return Mono.zip(list, a -> a[0])
        .cast(OrchestrationRequestContext.class)
        .doOnNext(this::updateStatus);
  }

  private void updateStatus(OrchestrationRequestContext ctx) {
    var allSuccess = this.orchestrators.stream().allMatch(o -> o.isSuccess().test(ctx));
    var status = allSuccess ? Status.SUCCESS : Status.FAILED;
    ctx.setStatus(status);
  }

}
