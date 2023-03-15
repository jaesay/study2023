package com.tobyspring.helloboot;

public class Hello {

  private final String name;
  private final int count;

  public Hello(String name, int count) {
    this.name = name;
    this.count = count;
  }

  public String getName() {
    return name;
  }

  public int getCount() {
    return count;
  }
}
