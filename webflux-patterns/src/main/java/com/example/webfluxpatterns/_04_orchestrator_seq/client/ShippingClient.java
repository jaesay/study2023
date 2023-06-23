package com.example.webfluxpatterns._04_orchestrator_seq.client;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.ShippingRequest;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.ShippingResponse;
import com.example.webfluxpatterns._04_orchestrator_seq.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ShippingClient {

  private static final String SCHEDULE = "schedule";
  private static final String CANCEL = "cancel";
  private final WebClient client;

  public ShippingClient(@Value("${sec04.shipping.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<ShippingResponse> schedule(ShippingRequest request){
    return this.callShippingService(SCHEDULE, request);
  }

  public Mono<ShippingResponse> cancel(ShippingRequest request){
    return this.callShippingService(CANCEL, request);
  }

  private Mono<ShippingResponse> callShippingService(String endPoint, ShippingRequest request){
    return this.client
        .post()
        .uri(endPoint)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(ShippingResponse.class)
        .onErrorReturn(this.buildErrorResponse(request));
  }

  private ShippingResponse buildErrorResponse(ShippingRequest request){
    return ShippingResponse.create(
        request.getOrderId(),
        request.getQuantity(),
        Status.FAILED,
        null,
        null
    );
  }
}
