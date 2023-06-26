package com.example.webfluxpatterns._09_ratelimiter.dto;

import lombok.Data;

@Data
public class Product {

  private long id;
  private String category;
  private String description;
  private int price;
}
