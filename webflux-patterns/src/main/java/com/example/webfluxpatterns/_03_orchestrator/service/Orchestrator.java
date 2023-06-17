package com.example.webfluxpatterns._03_orchestrator.service;

import com.example.webfluxpatterns._03_orchestrator.dto.OrchestrationRequestContext;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Orchestrator {

  public abstract Mono<OrchestrationRequestContext> create(OrchestrationRequestContext ctx);
  public abstract Predicate<OrchestrationRequestContext> isSuccess();
  public abstract Consumer<OrchestrationRequestContext> cancel();

}
