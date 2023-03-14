package com.tobyspring.config.autoconfig;

import com.tobyspring.config.MyAutoConfiguration;
import com.tobyspring.config.MyConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@MyAutoConfiguration
@MyConditionalOnClass("org.apache.catalina.startup.Tomcat")
public class TomcatWebServerConfig {

  @Bean("TomcatWebServerFactory")
  @ConditionalOnMissingBean
  public ServletWebServerFactory servletWebServerFactory(Environment env) {
    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    factory.setContextPath(env.getProperty("contextPath"));
    return factory;
  }
}
