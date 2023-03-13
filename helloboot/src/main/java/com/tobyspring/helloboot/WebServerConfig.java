package com.tobyspring.helloboot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class WebServerConfig {

  @Bean
  public ServletWebServerFactory customWebServerFactory() {
    TomcatServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();
    serverFactory.setPort(9090);
    return serverFactory;
  }
}
