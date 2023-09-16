package com.example.hexarch.member.application.port.in;

import com.example.hexarch.member.domain.Member;

public interface GetMemberQuery {

  Member getMember(long memberId);
}
