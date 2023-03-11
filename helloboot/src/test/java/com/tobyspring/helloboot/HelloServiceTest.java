package com.tobyspring.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HelloServiceTest {

  @Test
  void simpleHelloService() {
    SimpleHelloService helloService = new SimpleHelloService();

    String result = helloService.sayHello("Test");

    Assertions.assertThat(result).isEqualTo("Hello Test");
  }

}