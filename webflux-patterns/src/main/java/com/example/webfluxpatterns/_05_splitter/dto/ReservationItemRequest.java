package com.example.webfluxpatterns._05_splitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class ReservationItemRequest {

  private ReservationType type;
  private String category;
  private String city;
  private LocalDate from;
  private LocalDate to;

}
