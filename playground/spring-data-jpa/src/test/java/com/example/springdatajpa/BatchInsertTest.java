package com.example.springdatajpa;

import com.example.springdatajpa.entity.Member;
import com.example.springdatajpa.entity.Team;
import com.example.springdatajpa.repository.MemberRepository;
import com.example.springdatajpa.repository.TeamRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters"})
public class BatchInsertTest {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private TeamRepository teamRepository;
  @Autowired
  private EntityManager em;

  @Test
  void batch_insert_동작_확인() {

    for (int i = 0; i < 10; i++) {
      Team team = new Team("TEAM" + i);
      Member member = new Member("MEMBER" + i, 10, team);
      teamRepository.save(team);
      memberRepository.save(member);
    }

    em.flush();
  }
}
