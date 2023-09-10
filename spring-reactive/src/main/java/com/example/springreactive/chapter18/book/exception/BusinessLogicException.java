package com.example.springreactive.chapter18.book.exception;

import lombok.Getter;

public class BusinessLogicException extends RuntimeException {

  @Getter
  private ExceptionCode exceptionCode;

  public BusinessLogicException(ExceptionCode exceptionCode) {
    super(exceptionCode.getMessage());
    this.exceptionCode = exceptionCode;
  }
}
