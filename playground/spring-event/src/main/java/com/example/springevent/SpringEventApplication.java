package com.example.springevent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringEventApplication {

  public static void main(String[] args) {
    SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(SpringEventApplication.class);
    SpringApplication application = appBuilder.build();
    application.addListeners(new ApplicationEventListener());

    ConfigurableApplicationContext ctxt = application.run(args);
  }

}
