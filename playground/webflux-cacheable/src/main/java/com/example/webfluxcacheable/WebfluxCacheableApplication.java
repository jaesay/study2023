package com.example.webfluxcacheable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WebfluxCacheableApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebfluxCacheableApplication.class, args);
  }

}
