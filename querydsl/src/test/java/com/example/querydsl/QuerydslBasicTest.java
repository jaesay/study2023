package com.example.querydsl;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.team;
import static com.querydsl.jpa.JPAExpressions.select;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.querydsl.dto.MemberDto;
import com.example.querydsl.dto.QMemberDto;
import com.example.querydsl.dto.UserDto;
import com.example.querydsl.entity.Member;
import com.example.querydsl.entity.QMember;
import com.example.querydsl.entity.QTeam;
import com.example.querydsl.entity.Team;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import org.assertj.core.api.Assertions;
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

  @Test
  @DisplayName("조인 - on절")
  void join_on() {
    /* 1. 조인 대상 필터링 */
    /* 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회 */
    /*
     * 참고: on 절을 활용해 조인 대상을 필터링 할 때, 외부조인이 아니라 내부조인(inner join)을 사용하면, where 절에서 필터링 하는 것과 기능이 동일하다.
     * 따라서 on 절을 활용한 조인 대상 필터링을 사용할 때, 내부조인 이면 익숙한 where 절로 해결하고, 정말 외부조인이 필요한 경우에만 이 기능을 사용하자.
     * */
    List<Tuple> result = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(member.team, team).on(team.name.eq("teamA"))
        .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple = " + tuple);
    }

    /*
     * 하이버네이트 5.1부터 on 을 사용해서 서로 관계가 없는 필드로 외부 조인하는 기능이 추가되었다. 물론 내부 조인도 가능하다.
     * 주의! 문법을 잘 봐야 한다. leftJoin() 부분에 일반 조인과 다르게 엔티티 하나만 들어간다.
     * 일반조인: leftJoin(member.team, team)
     * on조인: from(member).leftJoin(team).on(xxx)
     * */
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    List<Tuple> result2 = queryFactory
        .select(member, team)
        .from(member)
        .leftJoin(team).on(member.username.eq(team.name))
        .fetch();

    for (Tuple tuple : result2) {
      System.out.println("t=" + tuple);
    }

  }

  @PersistenceUnit
  EntityManagerFactory emf;

  @Test
  @DisplayName("조인 - 페치 조인")
  void fetchJoinNo() {
    em.flush();
    em.clear();

    Member findMember = queryFactory
        .selectFrom(member)
        .where(member.username.eq("member1")).fetchOne();

    boolean loaded =
        emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();

    em.flush();
    em.clear();

    Member findMember2 = queryFactory
        .selectFrom(member)
        .join(member.team, team).fetchJoin().where(member.username.eq("member1")).fetchOne();

    boolean loaded2 = emf.getPersistenceUnitUtil().isLoaded(findMember2.getTeam());
    assertThat(loaded2).as("페치 조인 적용").isTrue();
  }


  /**
   * from 절의 서브쿼리 한계 JPA JPQL 서브쿼리의 한계점으로 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다. 당연히 Querydsl 도 지원하지 않는다.
   * 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지원한다. Querydsl도 하이버네이트 구현체를 사용하면 select 절의 서브쿼리를 지원한다. 하이버네이트
   * 5.1 이상에서 지원하지만 주의해서 사용하여야 한다.
   * <p>
   * from 절의 서브쿼리 해결방안 1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.) 2. 애플리케이션에서 쿼리를 2번 분리해서
   * 실행한다. 3. nativeSQL을 사용한다.
   */
  @Test
  @DisplayName("서브 쿼리")
  void subQuery() {
    /* where 절 서브쿼리 */
    QMember memberSub = new QMember("memberSub");

    List<Member> result = queryFactory
        .selectFrom(member)
        .where(member.age.eq(
            select(memberSub.age.max()).from(memberSub)
        ))
        .fetch();

    assertThat(result).extracting("age")
        .containsExactly(40);

    List<Member> result2 = queryFactory
        .selectFrom(member)
        .where(member.age.in(
            select(memberSub.age).from(memberSub)
                .where(memberSub.age.gt(10))))
        .fetch();

    assertThat(result2).extracting("age")
        .containsExactly(20, 30, 40);

    /* select 절에 subquery */
    List<Tuple> fetch = queryFactory
        .select(member.username,
            select(memberSub.age.avg()).from(memberSub))
        .from(member)
        .fetch();

    for (Tuple tuple : fetch) {
      System.out.println("username = " + tuple.get(member.username));
      System.out.println("age = " + tuple.get(select(memberSub.age.avg()).from(memberSub)));
    }
  }

  @Test
  @DisplayName("Case 문")
  void caseStmt() {
    /* simple case */
    List<String> results = queryFactory
        .select(member.age
            .when(10).then("열살")
            .when(20).then("스무살")
            .otherwise("기타"))
        .from(member)
        .fetch();

    /* complex case */
    List<String> results2 = queryFactory
        .select(new CaseBuilder()
            .when(member.age.between(0, 20)).then("0~20살")
            .when(member.age.between(21, 30)).then("21~30살").otherwise("기타"))
        .from(member)
        .fetch();

    /* orderBy에서 Case 문 함께 사용하기 */
    NumberExpression<Integer> rankPath = new CaseBuilder()
        .when(member.age.between(0, 20)).then(2)
        .when(member.age.between(21, 30)).then(1)
        .otherwise(3);

    List<Tuple> results3 = queryFactory
        .select(member.username, member.age, rankPath)
        .from(member)
        .orderBy(rankPath.desc())
        .fetch();

    for (Tuple tuple : results3) {
      String username = tuple.get(member.username);
      Integer age = tuple.get(member.age);
      Integer rank = tuple.get(rankPath);
      System.out.println("username = " + username + " age = " + age + " rank = " + rank);
    }
  }

  @Test
  @DisplayName("상수, 문자 더하기")
  void constant() {
    Tuple result = queryFactory
        .select(member.username, Expressions.constant("A"))
        .from(member)
        .fetchFirst();

    /*
     * member.age.stringValue() 부분이 중요한데, 문자가 아닌 다른 타입들은 stringValue() 로 문자로 변환할 수 있다.
     * 이 방법은 ENUM을 처리할 때도 자주 사용한다.
     * */
    String result2 = queryFactory
        .select(member.username.concat("_").concat(member.age.stringValue()))
        .from(member)
        .where(member.username.eq("member1"))
        .fetchOne();
  }

  @Test
  @DisplayName("프로젝션과 결과 반환 - 기본")
  void simpleProjection() {
    /* select 대상 지정 - 대상이 하나 */
    List<String> result = queryFactory
        .select(member.username)
        .from(member)
        .fetch();

    /* 튜플 조회 - 프로젝션 대상이 둘 이상일 때 사용 */
    List<Tuple> result2 = queryFactory
        .select(member.username, member.age).from(member)
        .fetch();

    for (Tuple tuple : result2) {
      String username = tuple.get(member.username);
      Integer age = tuple.get(member.age);
      System.out.println("username=" + username);
      System.out.println("age=" + age);
    }
  }

  @Test
  @DisplayName("프로젝션과 결과 반환 - DTO 조회")
  void dtoProjection() {
    /*
     * 순수 JPA에서 DTO 조회
     * 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야함
     * DTO의 package이름을 다 적어줘야해서 지저분함
     * 생성자 방식만 지원함
     * */
    List<MemberDto> result = em.createQuery(
            "select new com.example.querydsl.dto.MemberDto(m.username, m.age) from Member m",
            MemberDto.class)
        .getResultList();

    /* Querydsl 결과를 DTO 반환할 때 다음 3가지 방법 지원 */
    List<MemberDto> result2 = queryFactory
        .select(Projections.bean(MemberDto.class, member.username,
            member.age))
        .from(member)
        .fetch();

    /* 프로퍼티 접근 - Setter */
    List<MemberDto> result3 = queryFactory
        .select(Projections.fields(MemberDto.class, member.username,
            member.age))
        .from(member)
        .fetch();

    /*
     * 별칭이 다를 때
     * 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 해결 방안
     * ExpressionUtils.as(source,alias) : 필드나, 서브 쿼리에 별칭 적용
     * username.as("memberName") : 필드에 별칭 적용
     */
    QMember memberSub = new QMember("memberSub");
    List<UserDto> fetch = queryFactory
        .select(Projections.fields(UserDto.class, member.username.as("name"),
            ExpressionUtils.as(
                JPAExpressions.select(memberSub.age.max()).from(memberSub), "age"))
        ).from(member)
        .fetch();

    /* 필드 직접 접근 */
    List<MemberDto> result4 = queryFactory
        .select(Projections.constructor(MemberDto.class, member.username,
            member.age))
        .from(member)
        .fetch();
  }

  @Test
  @DisplayName("프로젝션과 결과 반환 - @QueryProjection")
  void queryProjection() {
    /*
    * @QueryProjection 활용
    * 이 방법은 컴파일러로 타입을 체크할 수 있으므로 가장 안전한 방법이다.
    * 다만 DTO에 QueryDSL  어노테이션을 유지해야 하는 점과 DTO까지 Q 파일을 생성해야 하는 단점이 있다.
    * */
    List<MemberDto> result = queryFactory
        .select(new QMemberDto(member.username, member.age))
        .from(member)
        .fetch();

    /*
    * distinct
    * 참고: distinct는 JPQL의 distinct와 같다.
    * */
    List<String> result2 = queryFactory
        .select(member.username).distinct()
        .from(member)
        .fetch();
  }

  @Test
  @DisplayName("동적 쿼리 - BooleanBuilder 사용")
  void dynamicQuery_BooleanBuilder() {
    /* 동적 쿼리를 해결하는 두가지 방식 - BooleanBuilder */
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember1(usernameParam, ageParam);

    Assertions.assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember1(String usernameCond, Integer ageCond) {
    BooleanBuilder builder = new BooleanBuilder();
    if (usernameCond != null) {
      builder.and(member.username.eq(usernameCond));
    }
    if (ageCond != null) {
      builder.and(member.age.eq(ageCond));
    }

    return queryFactory
        .selectFrom(member)
        .where(builder)
        .fetch();
  }

  @Test
  @DisplayName("동적 쿼리 - Where 다중 파라미터 사용 ")
  void dynamicQuery_WhereParam() {
    /*
    * 동적 쿼리를 해결하는 두가지 방식 - Where 다중 파라미터 사용
    * where 조건에 null 값은 무시된다.
    * 메서드를 다른 쿼리에서도 재활용 할 수 있다.
    * 쿼리 자체의 가독성이 높아진다.
    * */
    String usernameParam = "member1";
    Integer ageParam = 10;

    List<Member> result = searchMember2(usernameParam, ageParam);

    Assertions.assertThat(result.size()).isEqualTo(1);
  }

  private List<Member> searchMember2(String usernameCond, Integer ageCond) {
    return queryFactory
        .selectFrom(member)
        .where(usernameEq(usernameCond), ageEq(ageCond))
        .fetch();
  }

  private BooleanExpression usernameEq(String usernameCond) {
    return usernameCond != null ? member.username.eq(usernameCond) : null;
  }

  private BooleanExpression ageEq(Integer ageCond) {
    return ageCond != null ? member.age.eq(ageCond) : null;
  }

  private BooleanExpression allEq(String usernameCond, Integer ageCond) {
    /*
    * 조합 가능
    * null 체크는 주의해서 처리해야함
    * */
    return usernameEq(usernameCond).and(ageEq(ageCond));
  }

  @Test
  @DisplayName("수정, 삭제 벌크 연산")
  void bulk() {
    /*
    * 주의: JPQL 배치와 마찬가지로, 영속성 컨텍스트에 있는 엔티티를 무시하고 실행되기 때문에
    * 배치 쿼리를 실행하고 나면 영속성 컨텍스트를 초기화 하는 것이 안전하다.
    * */
    /* 쿼리 한번으로 대량 데이터 수정 */
    long count = queryFactory
        .update(member)
        .set(member.username, "비회원")
        .where(member.age.lt(28))
        .execute();

    em.flush();
    em.clear();

    /* 기존 숫자에 1 더하기 */
    long count2 = queryFactory
        .update(member)
        .set(member.age, member.age.add(1))
        .execute();

    em.flush();
    em.clear();

    /* 쿼리 한번으로 대량 데이터 삭제 */
    long count3 = queryFactory
        .delete(member)
        .where(member.age.gt(18))
        .execute();

    em.flush();
    em.clear();
  }
}

