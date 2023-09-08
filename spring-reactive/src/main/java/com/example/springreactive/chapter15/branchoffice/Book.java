package com.example.springreactive.chapter15.branchoffice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
class Book {

  private long bookId;
  private String name;
  private int price;
}
