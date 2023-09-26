package com.example.webfluxcacheable.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation used for async caching
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AsyncCacheable {

  String name() default "default1000Item5MinuteCache";

  long maximumSize() default 1000L;

  long expireAfterWriteSeconds() default 300L;
}
