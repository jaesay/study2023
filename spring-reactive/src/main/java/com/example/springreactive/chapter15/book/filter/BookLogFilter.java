package com.example.springreactive.chapter15.book.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BookLogFilter implements WebFilter {

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();
    return chain.filter(exchange).doAfterTerminate(() -> {
      if (path.contains("books")) {
        log.info("BookLogFilter#filter path: {}, status: {}", path, exchange.getResponse().getStatusCode());
      }
    });
  }
}
