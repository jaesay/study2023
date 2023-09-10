package com.example.springreactive.chapter19.book;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Book {

  @Id
  private long bookId;
  private String titleKorean;
  private String titleEnglish;
  private String description;
  private String author;
  private String isbn;
  private String publishDate;

  @CreatedDate
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column("last_modified_at")
  private LocalDateTime modifiedAt;
}
