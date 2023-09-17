package com.example.hexarch.member.application.service;

import com.example.hexarch.common.QueryService;
import com.example.hexarch.member.application.port.in.GetMemberQuery;
import com.example.hexarch.member.application.port.out.FindMemberPort;
import com.example.hexarch.member.domain.Member;
import lombok.RequiredArgsConstructor;

@QueryService
@RequiredArgsConstructor
class GetMemberService implements GetMemberQuery {

  private final FindMemberPort findMemberPort;

  @Override
  public Member getMember(long memberId) {
    return null;
  }
}
