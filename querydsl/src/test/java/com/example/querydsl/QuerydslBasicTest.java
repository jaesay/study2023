package com.example.querydsl;

import static com.example.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

  @Autowired
  EntityManager em;
  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);
    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);
    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
  }

  @Test
  void startJPQL() {
    String qlString = "select m from Member m " +
        "where m.username = :username";

    Member findMember = em.createQuery(qlString, Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void startQuerydsl() {
    Member findMember = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void search() {
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")
            .and(member.age.eq(10)))
        .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void searchAndParam() {
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"),
            member.age.eq(10))
        .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  void resultFetch() {
    List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

    Member findMember1 = queryFactory
        .selectFrom(member)
        .where(member.id.eq(1L))
        .fetchOne();

    Member findMember2 = queryFactory
        .selectFrom(member)
        .fetchFirst();

    QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();

    long count = queryFactory
        .selectFrom(member)
        .fetchCount();

  }
}


