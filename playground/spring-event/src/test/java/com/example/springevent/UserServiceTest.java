package com.example.springevent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class UserServiceTest {

  @Autowired
  UserService userService;

  @Test
  void createUser() {
    userService.createUser("test", "test@email.com");
  }

}