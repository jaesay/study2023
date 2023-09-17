package com.example.hexarch.member.application.service;

import com.example.hexarch.common.UseCase;
import com.example.hexarch.member.application.port.in.RegisterMemberCommand;
import com.example.hexarch.member.application.port.in.RegisterMemberUseCase;
import com.example.hexarch.member.application.port.out.FindMemberPort;
import com.example.hexarch.member.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
class RegisterMemberService implements RegisterMemberUseCase {

  private final FindMemberPort findMemberPort;
  private final SaveMemberPort saveMemberPort;

  @Override
  public boolean registerMember(RegisterMemberCommand command) {
    // TODO 입력을 받는다.
    // TODO 비즈니스 규칙을 검증한다.
    // TODO 모델 상태를 조작한다.
    // TODO 출력을 반환한다.
    return false;
  }
}
