package com.example.webfluxpatterns._04_orchestrator_seq.util;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.InventoryRequest;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.PaymentRequest;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.ShippingRequest;

public class OrchestrationUtil {

  public static void buildPaymentRequest(OrchestrationRequestContext ctx){
    var paymentRequest = PaymentRequest.create(
        ctx.getOrderRequest().getUserId(),
        ctx.getProductPrice() * ctx.getOrderRequest().getQuantity(),
        ctx.getOrderId()
    );
    ctx.setPaymentRequest(paymentRequest);
  }

  public static void buildInventoryRequest(OrchestrationRequestContext ctx){
    var inventoryRequest = InventoryRequest.create(
        ctx.getPaymentResponse().getPaymentId(),
        ctx.getOrderRequest().getProductId(),
        ctx.getOrderRequest().getQuantity()
    );
    ctx.setInventoryRequest(inventoryRequest);
  }

  public static void buildShippingRequest(OrchestrationRequestContext ctx){
    var shippingRequest = ShippingRequest.create(
        ctx.getOrderRequest().getQuantity(),
        ctx.getOrderRequest().getUserId(),
        ctx.getInventoryResponse().getInventoryId()
    );
    ctx.setShippingRequest(shippingRequest);
  }
}
