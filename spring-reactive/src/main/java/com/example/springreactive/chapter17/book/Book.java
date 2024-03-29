package com.example.springreactive.chapter17.book;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
class Book {

  private long bookId;
  private String titleKorean;
  private String titleEnglish;
  private String description;
  private String author;
  private String isbn;
  private String publishDate;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
