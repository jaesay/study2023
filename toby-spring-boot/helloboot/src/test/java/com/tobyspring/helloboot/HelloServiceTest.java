package com.tobyspring.helloboot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class HelloServiceTest {

  @Test
  void simpleHelloService() {
    SimpleHelloService helloService = new SimpleHelloService(helloRepositoryStub);

    String result = helloService.sayHello("Test");

    Assertions.assertThat(result).isEqualTo("Hello Test");
  }

  @Test
  void helloDecorator() {
    HelloDecorator helloDecorator = new HelloDecorator(name -> name);

    String result = helloDecorator.sayHello("Test");

    Assertions.assertThat(result).isEqualTo("*Test*");
  }

  private final HelloRepository helloRepositoryStub = new HelloRepository() {
    @Override
    public Hello findHello(String name) {
      return null;
    }

    @Override
    public void increaseCount(String name) {

    }
  };
}