package com.example.springcloudvaultexample;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class MyAppSecret {
  @Value("username")
  private String username;
  @Value("password")
  private String password;
}
