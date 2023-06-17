package com.example.webfluxpatterns._01_aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class PromotionResponse {

  private long id;
  private String type;
  private Double discount;
  private LocalDate endDate;
}
