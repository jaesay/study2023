package com.optimagrowth.gateway.filters;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(1)
@Component
public class TrackingFilter implements GlobalFilter {

  private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

  private final FilterUtils filterUtils;

  public TrackingFilter(FilterUtils filterUtils) {
    this.filterUtils = filterUtils;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    HttpHeaders requestHeaders = exchange.getRequest().getHeaders();
    if (isCorrelationIdPresent(requestHeaders)) {
      logger.debug("tmx-correlation-id found in tracking filter: {}. ",
          filterUtils.getCorrelationId(requestHeaders));
    } else {
      String correlationID = generateCorrelationId();
      exchange = filterUtils.setCorrelationId(exchange, correlationID);
      logger.debug("tmx-correlation-id generated in tracking filter: {}.", correlationID);
    }

    return chain.filter(exchange);
  }


  private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
    if (filterUtils.getCorrelationId(requestHeaders) != null) {
      return true;
    } else {
      return false;
    }
  }

  private String generateCorrelationId() {
    return java.util.UUID.randomUUID().toString();
  }

  private String getUsername(HttpHeaders requestHeaders) {
    String username = "";
    if (filterUtils.getAuthToken(requestHeaders) != null) {
      String authToken = filterUtils.getAuthToken(requestHeaders)
          .replace("Bearer ", ""); // Authorization HTTP Header에서 토큰을 파싱한다.
      JSONObject jsonObj = decodeJWT(authToken);
      try {
        username = jsonObj.getString("preferred_username"); // JWT에서 preferred_username(로그인 ID)을 가져온다.
      } catch (Exception e) {
        logger.debug(e.getMessage());
      }
    }
    return username;
  }

  private JSONObject decodeJWT(String JWTToken) {
    String[] split_string = JWTToken.split("\\.");
    String base64EncodedBody = split_string[1]; // Base64 인코딩을 사용하여 토큰을 파싱하고 토큰을 서명하는 키를 전달한다.
    Base64 base64Url = new Base64(true);
    String body = new String(base64Url.decode(base64EncodedBody)); // preferred_username을 조회하고자 JWT 본문을 JSON 객체로 파싱한다.
    return new JSONObject(body);
  }
}
