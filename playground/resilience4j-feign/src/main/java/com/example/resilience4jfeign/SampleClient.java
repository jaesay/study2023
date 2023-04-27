package com.example.resilience4jfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
    name = "sampleClient",
    url = "http://localhost:8081",
    fallbackFactory = SampleClientFallback.class
)
public interface SampleClient {

  @GetMapping("/success")
  String success();

  @GetMapping("/fail")
  String fail();
}

