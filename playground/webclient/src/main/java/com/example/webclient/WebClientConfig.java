package com.example.webclient;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final WebClientProperties props;

  @Bean
  public ReactorResourceFactory resourceFactory() {
    ConnectionProvider provider = ConnectionProvider.builder("custom-global")
        .maxConnections(props.pool().maxConnections())
        .pendingAcquireMaxCount(props.pool().pendingAcquireMaxCount())
        .maxIdleTime(props.pool().maxIdleTime())
        .maxLifeTime(props.pool().maxLifeTime())
        .evictInBackground(props.pool().evictInBackground())
        .metrics(props.pool().metricsEnabled())
        .build();

    ReactorResourceFactory factory = new ReactorResourceFactory();
    factory.setConnectionProvider(provider);
    factory.setUseGlobalResources(false);
    return factory;
  }

  @Bean
  public WebClient webClient2() {
    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
        .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .serializationInclusion(Include.NON_NULL)
        .build();

    ClientHttpConnector connector = new ReactorClientHttpConnector(
        resourceFactory(), client -> {
      client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.connectionTimeout());
      client.doOnConnected(conn -> conn
          .addHandlerLast(new ReadTimeoutHandler(props.readTimeout()))
      );
      return client
      // .wiretap("reactor.netty.http.client.HttpClient", LogLevel.INFO, AdvancedByteBufFormat.TEXTUAL)
      ;
    });

    return WebClient.builder()
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .codecs(configurer -> {
          configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
          configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        })
        .build();
  }

  @Bean
  @Primary
  public WebClient webClient() {
    ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
        .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .serializationInclusion(Include.NON_NULL)
        .build();

    ConnectionProvider provider = ConnectionProvider.builder("webclient")
        .maxConnections(props.pool().maxConnections())
        .pendingAcquireMaxCount(props.pool().pendingAcquireMaxCount())
        .maxIdleTime(props.pool().maxIdleTime())
        .maxLifeTime(props.pool().maxLifeTime())
        .evictInBackground(props.pool().evictInBackground())
        .metrics(props.pool().metricsEnabled())
        .build();

    HttpClient httpClient = HttpClient.create(provider)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.connectionTimeout())
        .doOnConnected(conn -> conn
            .addHandlerLast(new ReadTimeoutHandler(props.readTimeout()))
        );

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient.wiretap(true));

    return WebClient.builder()
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .codecs(configurer -> {
          configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
          configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
        })
        .build();
  }
}
