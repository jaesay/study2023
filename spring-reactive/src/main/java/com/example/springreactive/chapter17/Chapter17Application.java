package com.example.springreactive.chapter17;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.springreactive.chapter17.book")
public class Chapter17Application {

  public static void main(String[] args) {
    SpringApplication.run(Chapter17Application.class, args);
  }
}
