package com.example.springdocopenapispringboot3webmvc;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

  @GetMapping("/{username}")
  public ResponseEntity<User> getUser(@PathVariable String username) {
    User user = new User();
    user.setId(1L);
    user.setUsername(username);
    user.setFirstName("first name");
    user.setLastName("last name");
    user.setEmail("example@email.com");
    user.setPassword("1234");
    user.setUserStatus(1);

    return ResponseEntity.ok(user);
  }

  @PostMapping
  public ResponseEntity<User> createUser(@RequestBody User user) {
    return ResponseEntity.created(URI.create("/users/" + user.getUsername())).body(user);
  }

  @PutMapping("/{username}")
  public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User user) {
    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{username}")
  public ResponseEntity<User> deleteUser(@PathVariable String username) {
    return ResponseEntity.noContent().build();
  }

}
