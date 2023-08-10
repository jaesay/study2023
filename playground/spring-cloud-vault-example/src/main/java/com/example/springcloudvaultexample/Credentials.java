package com.example.springcloudvaultexample;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Credentials {

  @Value("${redisPassword}")
  private String redisPassword;
}
