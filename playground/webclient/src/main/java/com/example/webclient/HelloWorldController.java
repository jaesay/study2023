package com.example.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class HelloWorldController {

  private final HelloWorldService service;

  @GetMapping("/v1/hello-world")
  public Mono<ResponseEntity<String>> helloWorld() {
    return service.helloWorld()
        .map(ResponseEntity::ok);
  }
}
