package com.example.resilience4jfeign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SampleClientFallback implements FallbackFactory<SampleClient> {

  @Override
  public SampleClient create(Throwable cause) {
    return new SampleClient() {
      @Override
      public String success() {
        log.error("SampleClient#success: " + cause.getMessage());
        return null;
      }

      @Override
      public String fail() {
        log.error("SampleClient#fail: " + cause.getMessage());
        return null;
      }
    };
  }
}

