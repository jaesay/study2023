package com.example.webfluxpatterns._04_orchestrator_seq.service;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrderRequest;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrderResponse;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
import com.example.webfluxpatterns._04_orchestrator_seq.util.DebugUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

  private final OrderFulfillmentService fulfillmentService;
  private final OrderCancellationService cancellationService;

  public Mono<OrderResponse> placeOrder(Mono<OrderRequest> mono) {
    return mono
        .map(OrchestrationRequestContext::new)
        .flatMap(fulfillmentService::placeOrder)
        .doOnNext(this::doOrderPostProcessing)
        .doOnNext(DebugUtil::print)
        .map(this::toOrderResponse);
  }

  private void doOrderPostProcessing(OrchestrationRequestContext ctx) {
    if (Status.FAILED.equals(ctx.getStatus())) {
      this.cancellationService.cancelOrder(ctx);
    }
  }

  private OrderResponse toOrderResponse(OrchestrationRequestContext ctx) {
    var isSuccess = Status.SUCCESS.equals(ctx.getStatus());
    var address = isSuccess ? ctx.getShippingResponse().getAddress() : null;
    var deliveryDate = isSuccess ? ctx.getShippingResponse().getExpectedDelivery() : null;

    return OrderResponse.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderId(),
        ctx.getStatus(),
        address,
        deliveryDate
    );
  }
}
