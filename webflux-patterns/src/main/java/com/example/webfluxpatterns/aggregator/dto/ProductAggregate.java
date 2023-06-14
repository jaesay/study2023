package com.example.webfluxpatterns.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class ProductAggregate {

  private long id;
  private String category;
  private String description;
  private Price price;
  private List<Review> reviews;

  @Data
  public static class Price {
    private int listPrice;
    private Double discount;
    private Double discountedPrice;
    private Double amountSaved;
    private LocalDate endDate;
  }
}
