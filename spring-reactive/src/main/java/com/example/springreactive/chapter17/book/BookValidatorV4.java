package com.example.springreactive.chapter17.book;

import java.util.Set;
import javax.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolation;

@Component
@Slf4j
public class BookValidatorV4<T> {

  private final Validator validator;

  public BookValidatorV4(@Qualifier("javaxValidator") Validator validator) {
    this.validator = validator;
  }

  public void validate(T body) {
    Set<ConstraintViolation<T>> constraintViolations = validator.validate(body);
    if (!constraintViolations.isEmpty()) {
      onValidationErrors(constraintViolations);
    }
  }

  private void onValidationErrors(Set<ConstraintViolation<T>> constraintViolations) {
    log.error(constraintViolations.toString());
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, constraintViolations.toString());
  }
}
