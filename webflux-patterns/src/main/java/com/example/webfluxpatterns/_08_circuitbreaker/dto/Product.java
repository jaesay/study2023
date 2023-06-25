package com.example.webfluxpatterns._08_circuitbreaker.dto;

import lombok.Data;

@Data
public class Product {

  private long id;
  private String category;
  private String description;
  private int price;
}
