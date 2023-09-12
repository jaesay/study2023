package com.example.springreactive.chapter21.book;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import com.example.springreactive.chapter18.book.BookDto.Response;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

@Configuration
public class BookRouter {
  @Bean
  public RouterFunction<?> routeBook(BookHandler handler) {
    return route()
        .POST("/books", handler::createBook)
        .PATCH("/books/{book-id}", handler::updateBook)
        .GET("/books", handler::getBooks)
        .GET("/books/{book-id}", handler::getBook)
        .build();
  }

  @Bean
  public RouterFunction<?> routeStreamingBook(BookService bookService, BookMapper mapper) {

    Flux<Response> flux = bookService.streamingBooks().map(book -> mapper.bookToResponse(book));
    return route(RequestPredicates.GET("/streaming-books"),
        request -> ServerResponse
            .ok()
            .contentType(MediaType.TEXT_EVENT_STREAM)
            .body(
                flux,
                BookDto.Response.class));
  }
}
