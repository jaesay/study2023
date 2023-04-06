package com.example.querydsl.repository;

import static com.example.querydsl.entity.QMember.member;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

  @Autowired
  EntityManager em;
  @Autowired
  MemberRepository memberRepository;

  @Test
  void searchTest() {
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

    MemberSearchCondition condition = new MemberSearchCondition();
    condition.setAgeGoe(35);
    condition.setAgeLoe(40);
    condition.setTeamName("teamB");

    List<MemberTeamDto> result = memberRepository.search(condition);

    assertThat(result).extracting("username").containsExactly("member4");
  }

  @Test
  void searchPageSimpleTest() {
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

    MemberSearchCondition condition = new MemberSearchCondition();
    PageRequest pageRequest = PageRequest.of(0, 3);
    Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageRequest);

    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getContent())
        .extracting("username")
        .containsExactly("member1", "member2", "member3");
  }

  @Test
  void searchPageComplexTest() {
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

    MemberSearchCondition condition = new MemberSearchCondition();
    PageRequest pageRequest = PageRequest.of(0, 3);
    Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, pageRequest);

    assertThat(result.getSize()).isEqualTo(3);
    assertThat(result.getContent())
        .extracting("username")
        .containsExactly("member1", "member2", "member3");
  }

  @Test
  void QuerydslPredicateExecutorTest() {
    /*
    * 스프링 데이터에서 제공하는 기능
    * 한계점
    * 조인X (묵시적 조인은 가능하지만 left join이 불가능하다.)
    * 클라이언트가 Querydsl에 의존해야 한다. 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다. 복잡한 실무환경에서 사용하기에는 한계가 명확하다.
    * */
    Iterable<Member> result = memberRepository.findAll(
        member.age.between(10, 40).and(member.username.eq("member1")));

    result.forEach(System.out::println);
  }
}