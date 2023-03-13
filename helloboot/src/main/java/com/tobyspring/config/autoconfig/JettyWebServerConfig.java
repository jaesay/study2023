package com.tobyspring.config.autoconfig;

import com.tobyspring.config.MyAutoConfiguration;
import com.tobyspring.config.MyConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

@MyAutoConfiguration
@MyConditionalOnClass("org.eclipse.jetty.server.Server")
public class JettyWebServerConfig {

  @Bean("JettyWebServerFactory")
  @ConditionalOnMissingBean
  public ServletWebServerFactory servletWebServerFactory() {
    return new JettyServletWebServerFactory();
  }
}
