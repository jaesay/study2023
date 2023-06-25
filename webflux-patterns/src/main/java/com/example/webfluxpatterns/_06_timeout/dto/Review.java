package com.example.webfluxpatterns._06_timeout.dto;

import lombok.Data;

@Data
public class Review {

  private long id;
  private String user;
  private Integer rating;
  private String comment;
}
