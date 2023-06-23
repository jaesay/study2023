package com.example.webfluxpatterns._04_orchestrator_seq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class Product {

  private Integer id;
  private String category;
  private String description;
  private Integer price;

}
