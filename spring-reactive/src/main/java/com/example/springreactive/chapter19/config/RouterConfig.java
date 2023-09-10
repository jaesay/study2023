package com.example.springreactive.chapter19.config;

import javax.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class RouterConfig {

  @Bean
  public Validator javaxValidator() {
    return new LocalValidatorFactoryBean();
  }
}
