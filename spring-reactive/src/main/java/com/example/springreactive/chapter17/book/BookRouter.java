package com.example.springreactive.chapter17.book;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class BookRouter {

  @Bean
  public RouterFunction<?> routeBookV1(BookHandler handler) {
    return route()
        .POST("/v1/books", handler::createBook)
        .PATCH("/v1/books/{book-id}", handler::updateBook)
        .GET("/v1/books", handler::getBooks)
        .GET("/v1/books/{book-id}", handler::getBook)
        .build();
  }

  @Bean
  public Validator springValidator() {
    return new LocalValidatorFactoryBean();
  }

  @Bean
  public Validator javaxValidator() {
    return new LocalValidatorFactoryBean();
  }
}
