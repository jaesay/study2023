package com.example.springreactive.chapter21.book;

import com.example.springreactive.chapter18.book.BookDto.Response;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

  Book bookPostToBook(BookDto.Post requestBody);

  Book bookPatchToBook(BookDto.Patch requestBody);

  Response bookToResponse(Book book);

  List<Response> booksToResponse(List<Book> books);
}
