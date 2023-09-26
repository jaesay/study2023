package com.example.webfluxcacheable.cache;

public class CacheException extends Exception {

  public CacheException(String errorMessage, Throwable err) {
    super(errorMessage, err);
  }
}
