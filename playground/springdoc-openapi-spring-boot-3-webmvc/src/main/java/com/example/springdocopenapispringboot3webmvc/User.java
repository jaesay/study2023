package com.example.springdocopenapispringboot3webmvc;

import lombok.Data;

@Data
public class User {

  private Long id;
  private String username;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private int userStatus;

}
