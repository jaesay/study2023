package com.example.querydsl;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.team;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.QTeam;
import com.example.querydsl.entity.Team;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class QuerydslBasicTest {

  @Autowired
  EntityManager em;
  /**
   * JPAQueryFactory를 필드로 제공하면 동시성 문제는 어떻게 될까? 동시성 문제는 JPAQueryFactory를 생성할 때 제공하는
   * EntityManager(em)에 달려있다. 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를
   * 제공하기 때문에, 동시성 문제는 걱정하지 않아도 된다.
   */
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

  /**
   * Querydsl은 JPQL 빌더 JPQL: 문자(실행 시점 오류), Querydsl: 코드(컴파일 시점 오류) JPQL: 파라미터 바인딩 직접, Querydsl: 파라미터
   * 바인딩 자동 처리
   */
  @Test
  @DisplayName("JPQL vs Querydsl")
  void startJPQL() {
    /* JPQL */
    String qlString = "select m from Member m " +
        "where m.username = :username";

    Member findMember = em.createQuery(qlString, Member.class)
        .setParameter("username", "member1")
        .getSingleResult();

    assertThat(findMember.getUsername()).isEqualTo("member1");

    /* Querydsl */
    /* 같은 테이블을 조인해야 하는 경우가 아니면 기본 인스턴스를 사용하자. */
//    QMember qMember = new QMember("m"); // 별칭 직접 지정
//    QMember qMember = QMember.member; // 기본 인스턴스 사용
    Member findMember2 = queryFactory
        .select(member)
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();

    assertThat(findMember2.getUsername()).isEqualTo("member1");
  }

  @Test
  @DisplayName("검색 조건 쿼리")
  void search() {
    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")
            .and(member.age.eq(10)))
        .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");

    /* AND 조건을 가변인수로 처리 */
    Member findMember2 = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1"),
            member.age.eq(10))
        .fetchOne();

    assertThat(findMember2.getUsername()).isEqualTo("member1");
  }

  @Test
  @DisplayName("결과 조회")
  void resultFetch() {
    /* 리스트 조회, 데이터 없으면 빈 리스트 반환 */
    List<Member> fetch = queryFactory
        .selectFrom(member)
        .fetch();

    /* fetchOne() : 단 건 조회
    결과가 없으면 : null
    결과가 둘 이상이면 : com.querydsl.core.NonUniqueResultException */
    Member findMember1 = queryFactory
        .selectFrom(member)
        .where(member.id.eq(1L))
        .fetchOne();

    /* limit(1).fetchOne() */
    Member findMember2 = queryFactory
        .selectFrom(member)
        .fetchFirst();

    /* 페이징 정보 포함, total count 쿼리 추가 실행 */
    QueryResults<Member> results = queryFactory
        .selectFrom(member)
        .fetchResults();

    /* count 쿼리로 변경해서 count 수 조회 */
    long count = queryFactory
        .selectFrom(member)
        .fetchCount();

  }

  /**
   * 회원 정렬 순서 1. 회원 나이 내림차순(desc) 2. 회원 이름 올림차순(asc) 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
   */
  @Test
  @DisplayName("정렬")
  void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast()).fetch();

    Member member5 = result.get(0);
    Member member6 = result.get(1);
    Member memberNull = result.get(2);
    assertThat(member5.getUsername()).isEqualTo("member5");
    assertThat(member6.getUsername()).isEqualTo("member6");
    assertThat(memberNull.getUsername()).isNull();
  }

  @Test
  @DisplayName("페이징")
  void paging() {
    List<Member> result = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1) // 0부터 시작(zero index)
        .limit(2) // 최대 2건 조회
        .fetch();
    assertThat(result.size()).isEqualTo(2);

    /* 전체 조회 수가 필요하면? */
    /* 주의: count 쿼리가 실행되니 성능상 주의! */
    /* 참고: 실무에서 페이징 쿼리를 작성할 때, 데이터를 조회하는 쿼리는 여러 테이블을 조인해야 하지만,
    count 쿼리는 조인이 필요 없는 경우도 있다.
    그런데 이렇게 자동화된 count 쿼리는 원본 쿼리와 같이 모두 조인을 해버리기 때문에 성능이 안나올 수 있다.
    count 쿼리에 조인이 필요없는 성능 최적화가 필요하다면, count 전용 쿼리를 별도로 작성해야 한다.*/
    QueryResults<Member> queryResults = queryFactory
        .selectFrom(member)
        .orderBy(member.username.desc())
        .offset(1)
        .limit(2)
        .fetchResults();

    assertThat(queryResults.getTotal()).isEqualTo(4);
    assertThat(queryResults.getLimit()).isEqualTo(2);
    assertThat(queryResults.getOffset()).isEqualTo(1);
    assertThat(queryResults.getResults().size()).isEqualTo(2);
  }

  @Test
  @DisplayName("집합")
  void aggregation() {
    List<Tuple> result = queryFactory
        .select(member.count(),
            member.age.sum(),
            member.age.avg(),
            member.age.max(),
            member.age.min())
        .from(member)
        .fetch();

    Tuple tuple = result.get(0);
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);

    List<Tuple> groupBy = queryFactory
        .select(team.name, member.age.avg())
        .from(member)
        .join(member.team, team)
        .groupBy(team.name)
        .fetch();

    Tuple teamA = groupBy.get(0);
    Tuple teamB = groupBy.get(1);

    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);
    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
  }

  @Test
  @DisplayName("조인 - 기본 조인")
  void join() {
    /* join() , innerJoin() : 내부 조인(inner join)
    leftJoin() : left 외부 조인(left outer join)
    rightJoin() : rigth 외부 조인(rigth outer join) */
    QMember member = QMember.member;
    QTeam team = QTeam.team;

    List<Member> result = queryFactory
        .selectFrom(member)
        .join(member.team, team)
        .where(team.name.eq("teamA"))
        .fetch();

    assertThat(result)
        .extracting("username")
        .containsExactly("member1", "member2");

    /* 세타 조인(연관관계가 없는 필드로 조인)
    회원의 이름이 팀 이름과 같은 회원 조회 */
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Member> result2 = queryFactory
        .select(member)
        .from(member, team)
        .where(member.username.eq(team.name))
        .fetch();

    assertThat(result2)
        .extracting("username")
        .containsExactly("teamA", "teamB");
  }

}

