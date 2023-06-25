package com.example.webfluxpatterns._07_retry.dto;

import lombok.Data;

@Data
public class Review {

  private long id;
  private String user;
  private Integer rating;
  private String comment;
}
