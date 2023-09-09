package com.example.springreactive.chapter16;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "com.example.springreactive.chapter16.v1")
@SpringBootApplication(scanBasePackages = "com.example.springreactive.chapter16.v2")
public class Chapter16Application {

  public static void main(String[] args) {
    SpringApplication.run(Chapter16Application.class, args);
  }
}
