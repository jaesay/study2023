package com.redislabs.edu.redi2read.controller;

import com.redislabs.edu.redi2read.model.User;
import com.redislabs.edu.redi2read.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping
  public Iterable<User> all(@RequestParam(defaultValue = "") String email) {
    if (email.isEmpty()) {
      return userRepository.findAll();
    } else {
      return Optional.ofNullable(userRepository.findFirstByEmail(email))
          .map(List::of)
          .orElse(Collections.emptyList());
    }
  }

}
