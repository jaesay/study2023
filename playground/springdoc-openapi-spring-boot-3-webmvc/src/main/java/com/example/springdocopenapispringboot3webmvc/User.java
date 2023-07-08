package com.example.springdocopenapispringboot3webmvc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "User")
public class User {

  @Schema(example = "10", description = "id")
  private Long id;
  @Schema(example = "theUser", description = "username")
  private String username;
  @Schema(example = "John", description = "firstName")
  private String firstName;
  @Schema(example = "James", description = "lastName")
  private String lastName;
  @Schema(example = "john@email.com", description = "email")
  private String email;
  @Schema(example = "12345", description = "password")
  private String password;
  @Schema(example = "1", description = "User Status")
  private int userStatus;

}
