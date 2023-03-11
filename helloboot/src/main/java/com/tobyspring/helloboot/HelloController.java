package com.tobyspring.helloboot;

import java.util.Objects;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

  private final HelloService helloService;

  public HelloController(HelloService helloService) {
    this.helloService = helloService;
  }

  @GetMapping("/hello")
  public String hello(String name) {
    if (!StringUtils.hasText(name)) {
      throw new IllegalArgumentException();
    }

    return this.helloService.sayHello(Objects.requireNonNull(name));
  }
}
