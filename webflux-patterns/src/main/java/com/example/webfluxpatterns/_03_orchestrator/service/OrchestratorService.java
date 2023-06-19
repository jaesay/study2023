package com.example.webfluxpatterns._03_orchestrator.service;

import com.example.webfluxpatterns._03_orchestrator.client.ProductClient;
import com.example.webfluxpatterns._03_orchestrator.dto.*;
import com.example.webfluxpatterns._03_orchestrator.util.OrchestrationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrchestratorService {

  private final ProductClient productClient;
  private final OrderFulfillmentService fulfillmentService;
  private final OrderCancellationService cancellationService;

  public Mono<OrderResponse> placeOrder(Mono<OrderRequest> mono) {
    return mono
        .map(OrchestrationRequestContext::new)
        .flatMap(this::getProduct)
        .doOnNext(OrchestrationUtil::buildRequestContext)
        .flatMap(fulfillmentService::placeOrder)
        .doOnNext(this::doOrderPostProcessing)
        .map(this::toOrderResponse);
  }

  private Mono<OrchestrationRequestContext> getProduct(OrchestrationRequestContext ctx) {
    return this.productClient.getProduct(ctx.getOrderRequest().getProductId())
        .map(Product::getPrice)
        .doOnNext(ctx::setProductPrice)
        .map(i -> ctx);
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
