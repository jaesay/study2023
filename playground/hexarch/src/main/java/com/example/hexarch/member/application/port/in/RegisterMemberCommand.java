package com.example.hexarch.member.application.port.in;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class RegisterMemberCommand {

  private final Long id;
  @NotEmpty
  private final String name;

  public RegisterMemberCommand(long id, String name) {
    this.id = id;
    this.name = name;
    // TODO 입력 유효성 검사
  }
}
