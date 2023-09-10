package com.example.springreactive.chapter18.book;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

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
}
