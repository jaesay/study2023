package com.example.abstractclasstestexample;

public abstract class AbstractMethodCalling {

  protected abstract String abstractFunc();

  public String defaultImpl() {
    String res = abstractFunc();
    return (res == null) ? "Default" : (res + " Default");
  }
}

