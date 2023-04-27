package com.example.resilience4jfeign;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/backend-a")
public class BackendAController {

  private final SampleClient client;

  @GetMapping("/success")
  public String success() {
    return client.success();
  }

  @GetMapping("/fail")
  public String fail() {
    return client.fail();
  }
}
