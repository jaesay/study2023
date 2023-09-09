package com.example.springreactive.chapter16.v2;

import org.mapstruct.Mapper;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
interface BookMapper {

  Book bookPostToBook(BookDto.Post requestBody);
  Book bookPatchToBook(BookDto.Patch requestBody);
  BookDto.Response bookToResponse(Book book);
}
