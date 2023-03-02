package com.optimagrowth.gateway.filters;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseFilter {

  private final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

  private final Tracer tracer; // trace id 및 span id 정보에 접근하는 진입점 설정

  public ResponseFilter(Tracer tracer) {
    this.tracer = tracer;
  }

  @Bean
  public GlobalFilter postGlobalFilter() {
    return (exchange, chain) -> {
      final String traceId = Optional.ofNullable(tracer.currentSpan())
          .map(Span::context)
          .map(TraceContext::traceIdString)
          .orElse("null");

      return chain.filter(exchange).then(Mono.fromRunnable(() -> {
        logger.debug("Adding the correlation id to the outbound headers. {}", traceId);
        exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, traceId);
        logger.debug("Completing outgoing request for {}.", exchange.getRequest().getURI());
      }));
    };
  }
}
