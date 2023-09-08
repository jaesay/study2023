package com.example.springreactive.chapter15.book.controller;

import com.example.springreactive.chapter15.book.dto.BookDto;
import com.example.springreactive.chapter15.book.dto.BookDto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/controller/books")
@Slf4j
class BookController {

  @GetMapping("/{book-id}")
  public Mono<Response> getBook(@PathVariable("book-id") long bookId) {
    log.info("BookController#getBook");
    return Mono.just(BookDto.Response.builder()
        .bookId(bookId)
        .bookName("Advanced Java")
        .author("Kevin")
        .isbn("111-11-1111-111-1").build());
  }
}
