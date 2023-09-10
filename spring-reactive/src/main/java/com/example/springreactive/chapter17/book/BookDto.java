package com.example.springreactive.chapter17.book;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

class BookDto {

  @Getter
  static class Post {

    @NotBlank
    private String titleKorean;
    @NotBlank
    private String titleEnglish;
    @NotBlank
    private String description;
    @NotBlank
    private String author;
    @NotBlank
    private String isbn;
    @NotBlank
    private String publishDate;
  }

  @Getter
  static class Patch {

    @Setter
    private long bookId;
    @NotBlank
    private String titleKorean;
    @NotBlank
    private String titleEnglish;
    @NotBlank
    private String description;
    @NotBlank
    private String author;
    @NotBlank
    private String isbn;
    @NotBlank
    private String publishDate;
  }

  @Builder
  @Getter
  static class Response {

    private long bookId;
    private String titleKorean;
    private String titleEnglish;
    private String description;
    private String author;
    private String isbn;
    private String publishDate;
  }
}
