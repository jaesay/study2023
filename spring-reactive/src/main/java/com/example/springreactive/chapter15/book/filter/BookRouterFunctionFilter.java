package com.example.springreactive.chapter15.book.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
public class BookRouterFunctionFilter implements HandlerFilterFunction {

  @Override
  public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction next) {
    String path = request.requestPath().value();

    return next.handle(request).doAfterTerminate(() -> {
      log.info("BookRouterFunctionFilter#filter path: {}, status: {}", path, request.exchange().getResponse().getStatusCode());
    });
  }
}
