package com.example.webfluxpatterns._02_scattergather.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FlightResult {

  private String airline;
  private String from;
  private String to;
  private double price;
  private LocalDate date;
}
