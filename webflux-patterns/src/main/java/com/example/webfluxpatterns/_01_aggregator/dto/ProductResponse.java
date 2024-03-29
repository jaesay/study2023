package com.example.webfluxpatterns._01_aggregator.dto;

import lombok.Data;

@Data
public class ProductResponse {

  private long id;
  private String category;
  private String description;
  private int price;
}
