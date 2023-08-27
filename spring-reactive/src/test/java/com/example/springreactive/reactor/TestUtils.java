package com.example.springreactive.reactor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TestUtils {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern("HH:mm:ss.SSS");

  public static void print(String msg, Object ... args) {
    System.out.printf(
        LocalTime.now()
            .format(DATE_TIME_FORMATTER) + " [" + Thread.currentThread().getName() + "] " + String.format(msg, args) + "%n");
  }

}
