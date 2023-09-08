package com.example.springreactive.chapter15.book.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import com.example.springreactive.chapter15.book.filter.BookRouterFunctionFilter;
import com.example.springreactive.chapter15.book.dto.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class BookRouterFunction {

  @Bean
  public RouterFunction routerFunction() {
    return RouterFunctions
        .route(GET("/v1/router/books/{book-id}"),
            (ServerRequest request) -> this.getBook(request))
        .filter(new BookRouterFunctionFilter());
  }

  public Mono<ServerResponse> getBook(ServerRequest request) {
    log.info("BookRouterFunction#getBook");
    return ServerResponse
        .ok()
        .body(Mono.just(BookDto.Response.builder()
            .bookId(Long.parseLong(request.pathVariable("book-id")))
            .bookName("Advanced Reactor")
            .author("Tom")
            .isbn("222-22-2222-222-2").build()), BookDto.Response.class);
  }
}
