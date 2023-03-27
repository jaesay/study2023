package com.example.querydsl.repository;

import static com.example.querydsl.entity.QMember.member;
import static com.example.querydsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

import com.example.querydsl.dto.MemberSearchCondition;
import com.example.querydsl.dto.MemberTeamDto;
import com.example.querydsl.dto.QMemberTeamDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;

/**
 * Spring Data의 기본 Repository 구현체 검색 전략 때문에 커스텀 인터페이스 이름에 "Impl" 접미사를 추가해야 한다.
 * 이러한 이름 규칙을 따르는 이유는 Spring Data가 애플리케이션 컨텍스트에서 커스텀 구현체를 자동으로 검색하고 프록시를 생성하여 기본 Repository 구현체와 결합할 수 있도록 하기 위함이다.
 * 이를 통해 커스텀 인터페이스와 기본 Repository 인터페이스가 동일한 프록시 객체를 통해 접근될 수 있다.
 */
public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemberRepositoryImpl(EntityManager em) {
    this.queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<MemberTeamDto> search(MemberSearchCondition condition) {
    /* 참고: where 절에 파라미터 방식을 사용하면 조건 재사용 가능 */
    return queryFactory
        .select(new QMemberTeamDto(
            member.id,
            member.username,
            member.age,
            team.id,
            team.name))
        .from(member)
        .leftJoin(member.team, team)
        .where(usernameEq(condition.getUsername()), teamNameEq(condition.getTeamName()),
            ageGoe(condition.getAgeGoe()),
            ageLoe(condition.getAgeLoe()))
        .fetch();
  }

  private BooleanExpression usernameEq(String username) {
    /* BooleanExpression 사용하면 조합해서 사용할 수 있다. */
    return hasText(username) ? member.username.eq(username) : null;
  }

  private BooleanExpression teamNameEq(String teamName) {
    return hasText(teamName) ? team.name.eq(teamName) : null;
  }

  private BooleanExpression ageGoe(Integer ageGoe) {
    return ageGoe == null ? null : member.age.goe(ageGoe);
  }

  private BooleanExpression ageLoe(Integer ageLoe) {
    return ageLoe == null ? null : member.age.loe(ageLoe);
  }
}
