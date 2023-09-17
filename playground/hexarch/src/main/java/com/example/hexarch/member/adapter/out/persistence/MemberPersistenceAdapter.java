package com.example.hexarch.member.adapter.out.persistence;

import com.example.hexarch.common.PersistenceAdapter;
import com.example.hexarch.member.application.port.out.FindMemberPort;
import com.example.hexarch.member.application.port.out.SaveMemberPort;
import com.example.hexarch.member.domain.Member;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@PersistenceAdapter
class MemberPersistenceAdapter implements FindMemberPort, SaveMemberPort {

  private final MemberRepository repository;
  private final MemberMapper memberMapper;

  @Override
  public Member findMember(long id) {
    return null;
  }

  @Override
  public boolean saveMember(Member member) {
    // TODO 입력을 받는다.
    // TODO 입력을 데이터베이스 포맷으로 매핑한다.
    // TODO 입력을 데이터베이스로 보낸다.
    // TODO 데이터베이스 출력을 애플리케이션 포맷으로 매핑한다.
    // TODO 출력을 반환한다.

    return false;
  }
}
