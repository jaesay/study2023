package com.example.webfluxpatterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.webfluxpatterns.aggregator")
public class WebfluxPatternsApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebfluxPatternsApplication.class, args);
  }

}