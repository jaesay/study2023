package com.example.springreactive.chapter17.book;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * 17.3 Custom Validator를 이용한 유효성 검증
 */
//@Component
public class BookValidatorV2 implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return BookDto.Post.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    BookDto.Post post = (BookDto.Post) target;

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors, "titleKorean", "field.required");

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors, "titleEnglish", "field.required");
  }
}
