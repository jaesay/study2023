package com.example.webfluxpatterns._03_orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class OrderRequest {

  private Integer userId;
  private Integer productId;
  private Integer quantity;

}
