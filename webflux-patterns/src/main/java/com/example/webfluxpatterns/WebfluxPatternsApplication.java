package com.example.webfluxpatterns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.webfluxpatterns._09_ratelimiter")
public class WebfluxPatternsApplication {

  public static void main(String[] args) {
    SpringApplication.run(WebfluxPatternsApplication.class, args);
  }

}
