package com.example.springreactive.chapter19;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@EnableR2dbcRepositories
@EnableR2dbcAuditing
@SpringBootApplication(scanBasePackages = "com.example.springreactive.chapter19.book")
public class Chapter19Application {

  public static void main(String[] args) {
    SpringApplication.run(Chapter19Application.class, args);
  }
}
