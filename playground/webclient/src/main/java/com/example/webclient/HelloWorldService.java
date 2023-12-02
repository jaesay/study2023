package com.example.webclient;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.function.TupleUtils;

@Service
@RequiredArgsConstructor
public class HelloWorldService {

  private final HelloClient helloClient;
  private final WorldClient worldClient;

  public Mono<String> helloWorld() {
    return Mono.zip(helloClient.getHello(), worldClient.getWorld())
        .map(TupleUtils.function((hello, world) -> String.format("%s %s", hello, world)));
  }
}
