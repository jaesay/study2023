package com.optimagrowth.license.config;

import com.optimagrowth.license.utils.UserContextInterceptor;
import java.util.Locale;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class WebConfig {

  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setUseCodeAsDefaultMessage(true);
    messageSource.setBasenames("messages");
    return messageSource;
  }

  @LoadBalanced
  @Bean
  public RestTemplate getRestTemplate(){
    var restTemplate = new RestTemplate();
    var interceptors = restTemplate.getInterceptors();
    interceptors.add(new UserContextInterceptor());
    restTemplate.setInterceptors(interceptors);
    return restTemplate;
  }
}
