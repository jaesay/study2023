package com.example.webfluxpatterns._03_orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PaymentResponse {

  private Integer userId;
  private String name;
  private Integer balance;
  private Status status;

}
