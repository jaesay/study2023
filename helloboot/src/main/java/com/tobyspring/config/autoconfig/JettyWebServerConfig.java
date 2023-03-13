package com.tobyspring.config.autoconfig;

import com.tobyspring.config.MyAutoConfiguration;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@MyAutoConfiguration
public class JettyWebServerConfig {

  @Bean("JettyWebServerFactory")
  public ServletWebServerFactory servletWebServerFactory() {
    return new JettyServletWebServerFactory();
  }
}
