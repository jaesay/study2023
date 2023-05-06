package com.example.tobyspringrp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteServiceApp {

  @RestController
  public static class MyController {

    @GetMapping("/service")
    public String service(String req) throws InterruptedException {
      Thread.sleep(2000);
//      throw new RuntimeException();
      return req + "/service1";
    }

    @GetMapping("/service2")
    public String service2(String req) throws InterruptedException {
      Thread.sleep(2000);
      return req + "/service2";
    }
  }

  public static void main(String[] args) {
    System.setProperty("server.port", "8081");
    System.setProperty("server.tomcat.threads.max", "1000");
    SpringApplication.run(RemoteServiceApp.class, args);
  }
}
