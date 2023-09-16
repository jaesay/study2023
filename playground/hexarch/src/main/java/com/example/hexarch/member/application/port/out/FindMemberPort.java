package com.example.hexarch.member.application.port.out;

import com.example.hexarch.member.domain.Member;

public interface FindMemberPort {

  Member findMember(long id);
}
