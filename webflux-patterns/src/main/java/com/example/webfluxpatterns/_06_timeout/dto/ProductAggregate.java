package com.example.webfluxpatterns._06_timeout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class ProductAggregate {

  private long id;
  private String category;
  private String description;
  private List<Review> reviews;
}
