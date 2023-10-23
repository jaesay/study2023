package com.reactivespring.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "webclient")
@ConstructorBinding
@RequiredArgsConstructor
@Getter
public class WebClientProperties {

  private final int connectionTimeout;
  private final int readTimeout;
}
