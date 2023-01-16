package com.example.springcloudvaultexample;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequiredArgsConstructor
public class SpringCloudVaultExampleApplication {
  private final MyAppSecret myAppSecret;

  @GetMapping("/")
  public String home() {
    return myAppSecret.toString();
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringCloudVaultExampleApplication.class, args);
  }

}
