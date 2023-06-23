package com.example.webfluxpatterns._04_orchestrator_seq.service;

import com.example.webfluxpatterns._04_orchestrator_seq.client.ProductClient;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Product;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
import com.example.webfluxpatterns._04_orchestrator_seq.util.OrchestrationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderFulfillmentService {

  private final ProductClient productClient;
  private final PaymentOrchestrator paymentOrchestrator;
  private final InventoryOrchestrator inventoryOrchestrator;
  private final ShippingOrchestrator shippingOrchestrator;

  public Mono<OrchestrationRequestContext> placeOrder(OrchestrationRequestContext ctx) {
    return this.getProduct(ctx)
        .doOnNext(OrchestrationUtil::buildPaymentRequest)
        .flatMap(this.paymentOrchestrator::create)
        .doOnNext(OrchestrationUtil::buildInventoryRequest)
        .flatMap(this.inventoryOrchestrator::create)
        .doOnNext(OrchestrationUtil::buildShippingRequest)
        .flatMap(this.shippingOrchestrator::create)
        .doOnNext(c -> c.setStatus(Status.SUCCESS))
        .doOnError(ex -> ctx.setStatus(Status.FAILED))
        .onErrorReturn(ctx);
  }

  private Mono<OrchestrationRequestContext> getProduct(OrchestrationRequestContext ctx) {
    return this.productClient.getProduct(ctx.getOrderRequest().getProductId())
        .map(Product::getPrice)
        .doOnNext(ctx::setProductPrice)
        .map(i -> ctx);
  }

}
