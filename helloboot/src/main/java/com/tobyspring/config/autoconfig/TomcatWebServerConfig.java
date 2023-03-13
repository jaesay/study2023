package com.tobyspring.config.autoconfig;

import com.tobyspring.config.MyAutoConfiguration;
import com.tobyspring.config.autoconfig.TomcatWebServerConfig.TomcatCondition;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;

@MyAutoConfiguration
@Conditional(TomcatCondition.class)
public class TomcatWebServerConfig {

  @Bean("TomcatWebServerFactory")
  public ServletWebServerFactory servletWebServerFactory() {
    return new TomcatServletWebServerFactory();
  }

  static class TomcatCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      return false;
    }
  }
}
