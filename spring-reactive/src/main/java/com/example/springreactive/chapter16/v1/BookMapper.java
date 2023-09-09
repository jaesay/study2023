package com.example.springreactive.chapter16.v1;

import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
interface BookMapper {

  Book bookPostToBook(BookDto.Post requestBody);
  Book bookPatchToBook(BookDto.Patch requestBody);
  BookDto.Response bookToResponse(Book book);
  default Mono<BookDto.Response> bookToBookResponse(Mono<Book> mono) {
    return mono.flatMap(book -> Mono.just(bookToResponse(book)));
  }
}
