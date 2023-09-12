package com.example.springreactive.chapter20;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.springreactive.chapter20")

public class Chapter20Application {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Chapter20Application.class);
    app.setDefaultProperties(Map.of("server.port", "8081"));
    app.run(args);
  }
}
