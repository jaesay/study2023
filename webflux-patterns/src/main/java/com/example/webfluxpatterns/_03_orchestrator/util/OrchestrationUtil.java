package com.example.webfluxpatterns._03_orchestrator.util;

import com.example.webfluxpatterns._03_orchestrator.dto.InventoryRequest;
import com.example.webfluxpatterns._03_orchestrator.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._03_orchestrator.dto.PaymentRequest;
import com.example.webfluxpatterns._03_orchestrator.dto.ShippingRequest;

public class OrchestrationUtil {

  public static void buildRequestContext(OrchestrationRequestContext ctx){
    buildPaymentRequest(ctx);
    buildInventoryRequest(ctx);
    buildShippingRequest(ctx);
  }

  private static void buildPaymentRequest(OrchestrationRequestContext ctx){
    var paymentRequest = PaymentRequest.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getProductPrice() * ctx.getOrderRequest().getQuantity(),
        ctx.getOrderId()
    );
    ctx.setPaymentRequest(paymentRequest);
  }

  private static void buildInventoryRequest(OrchestrationRequestContext ctx){
    var inventoryRequest = InventoryRequest.create(
        ctx.getOrderId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderRequest().getQuantity()
    );
    ctx.setInventoryRequest(inventoryRequest);
  }

  private static void buildShippingRequest(OrchestrationRequestContext ctx){
    var shippingRequest = ShippingRequest.create(
        ctx.getOrderRequest().getQuantity(),
        ctx.getOrderRequest().getUserId(),
        ctx.getOrderId()
    );
    ctx.setShippingRequest(shippingRequest);
  }
}
