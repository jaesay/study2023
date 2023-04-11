package com.redislabs.edu.redi2read;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Redi2readApplication {

  public static void main(String[] args) {
    SpringApplication.run(Redi2readApplication.class, args);
  }

}
