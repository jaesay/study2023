package com.example.springdocopenapispringboot3webmvc;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "user", description = "the user API")
public class UserController {

  @Operation(summary = "Get user by user name", tags = {"user"})
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "successful operation", content = {
          @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)),
      }),
      @ApiResponse(responseCode = "400", description = "Invalid username supplied", content = @Content),
      @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
  })
  @GetMapping("/{username}")
  public ResponseEntity<User> getUser(
      @Parameter(description = "The name that needs to be fetched. Use user1 for testing. ", required = true) @PathVariable String username) {

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
  @Operation(
      summary = "Create user",
      description = "This can only be done by the logged in user.",
      tags = {"user"}
  )
  @ApiResponses(value = {
      @ApiResponse(description = "successful operation",
          content = {
              @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
          })
  })
  public ResponseEntity<User> createUser(@RequestBody User user) {
    return ResponseEntity.created(URI.create("/users/" + user.getUsername())).body(user);
  }

  @Operation(
      summary = "Update user",
      description = "This can only be done by the logged in user.",
      tags = {"user"}
  )
  @ApiResponses(value = @ApiResponse(description = "successful operation"))
  @PutMapping("/{username}")
  public ResponseEntity<User> updateUser(
      @Parameter(description = "name that need to be deleted", required = true, explode = Explode.FALSE, in = ParameterIn.PATH, name = "username", style = ParameterStyle.SIMPLE, schema = @Schema(type = "string")) @PathVariable String username,
      @RequestBody User user) {
    return ResponseEntity.ok(user);
  }

  @Operation(
      summary = "Delete user",
      description = "This can only be done by the logged in user.",
      tags = {"user"}
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Invalid username supplied"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @DeleteMapping("/{username}")
  public ResponseEntity<User> deleteUser(
      @Parameter(description = "The name that needs to be deleted", required = true) @PathVariable String username) {

    return ResponseEntity.noContent().build();
  }

}
