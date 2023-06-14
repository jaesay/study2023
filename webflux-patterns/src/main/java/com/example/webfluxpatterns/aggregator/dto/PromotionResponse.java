package com.example.webfluxpatterns.aggregator.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PromotionResponse {

  private long id;
  private String type;
  private Double discount;
  private LocalDate endDate;
}
