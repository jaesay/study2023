package com.example.webfluxpatterns._05_splitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "create")
public class ReservationResponse {

  private UUID reservationId;
  private Integer price;
  private List<ReservationItemResponse> items;

}
