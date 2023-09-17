package com.example.hexarch.member.adapter.out.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

interface MemberRepository extends ReactiveCrudRepository<MemberEntity, Long> {

}
