package com.reactivespring.config;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final WebClientProperties props;

  @Bean
  public WebClient.Builder webClientBuilder() {
    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
        .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .serializationInclusion(Include.NON_NULL)
        .build();

    HttpClient httpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectionTimeout())
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(props.getReadTimeout()))
        );

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

    return WebClient.builder()
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .codecs(configurer -> {
          configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
          configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        });
  }

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder.build();
  }
}
