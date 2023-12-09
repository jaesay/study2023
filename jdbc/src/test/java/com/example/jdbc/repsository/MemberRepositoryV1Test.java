package com.example.jdbc.repsository;

import static com.example.jdbc.connection.ConnectionConst.PASSWORD;
import static com.example.jdbc.connection.ConnectionConst.URL;
import static com.example.jdbc.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.example.jdbc.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class MemberRepositoryV1Test {

  MemberRepositoryV1 repository;

  @BeforeEach
  void beforeEach() throws Exception {
    // 기본 DriverManager - 항상 새로운 커넥션 획득
    // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    // 커넥션 풀링: HikariProxyConnection -> JdbcConnection
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    repository = new MemberRepositoryV1(dataSource);
  }

  @Test
  void crud() throws SQLException, InterruptedException {
    // 같은 커넥션을 재사용하여도 커넥션을 프록시 감싸서 반환하여 커넥션은 같지만 다른 프록시 객체이다.
//    23:20:48.496 [main] INFO  c.e.j.repsository.MemberRepositoryV1 -- get connection=HikariProxyConnection@431506362 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
//    23:20:48.525 [main] INFO  c.e.j.repsository.MemberRepositoryV1 -- get connection=HikariProxyConnection@1129944640 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
//    23:20:48.533 [main] INFO  c.e.j.r.MemberRepositoryV1Test -- findMember=Member(memberId=memberV0, money=10000)
//    23:20:48.594 [main] INFO  c.e.j.repsository.MemberRepositoryV1 -- get connection=HikariProxyConnection@895766599 wrapping conn0: url=jdbc:h2:tcp://localhost/~/test user=SA, class=class com.zaxxer.hikari.pool.HikariProxyConnection
//    23:20:48.595 [HikariPool-1 housekeeper] DEBUG com.zaxxer.hikari.pool.HikariPool -- HikariPool-1 - Pool stats (total=1, active=1, idle=0, waiting=0)
//    23:20:48.597 [main] INFO  c.e.j.repsository.MemberRepositoryV1 -- resultSize=1

    // save
    Member member = new Member("memberV0", 10000);
    repository.save(member);

    // findById
    Member findMember = repository.findById(member.getMemberId());
    log.info("findMember={}", findMember);
    assertThat(findMember).isEqualTo(member);

    // update
    repository.update(member.getMemberId(), 20000);
    Member updatedMember = repository.findById(member.getMemberId());
    assertThat(updatedMember.getMoney()).isEqualTo(20000);

    // delete
    repository.delete(member.getMemberId());
    assertThatThrownBy(() -> repository.findById(member.getMemberId()))
        .isInstanceOf(NoSuchElementException.class);

    Thread.sleep(1000);
  }
}