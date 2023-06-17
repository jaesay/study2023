package com.example.webfluxpatterns._03_orchestrator.client;

import com.example.webfluxpatterns._03_orchestrator.dto.PaymentRequest;
import com.example.webfluxpatterns._03_orchestrator.dto.PaymentResponse;
import com.example.webfluxpatterns._03_orchestrator.dto.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserClient {

  private static final String DEDUCT = "deduct";
  private static final String REFUND = "refund";
  private final WebClient client;

  public UserClient(@Value("${sec03.user.service}") String baseUrl){
    this.client = WebClient.builder()
        .baseUrl(baseUrl)
        .build();
  }

  public Mono<PaymentResponse> deduct(PaymentRequest request){
    return this.callUserService(DEDUCT, request);
  }

  public Mono<PaymentResponse> refund(PaymentRequest request){
    return this.callUserService(REFUND, request);
  }

  private Mono<PaymentResponse> callUserService(String endPoint, PaymentRequest request){
    return this.client
        .post()
        .uri(endPoint)
        .bodyValue(request)
        .retrieve()
        .bodyToMono(PaymentResponse.class)
        .onErrorReturn(this.buildErrorResponse(request));
  }

  private PaymentResponse buildErrorResponse(PaymentRequest request){
    return PaymentResponse.create(
        request.getUserId(),
        null,
        null,
        Status.FAILED
    );
  }
}
