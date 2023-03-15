package com.tobyspring.helloboot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@HellobootTest
class HelloRepositoryTest {

  @Autowired
  JdbcTemplate jdbcTemplate;
  @Autowired
  HelloRepository helloRepository;

  @BeforeEach
  void init() {
    jdbcTemplate.execute("create table if not exists hello(name varchar(50) primary key, count int)");
  }

  @Test
  void findHelloFailed() {
    assertThat(helloRepository.findHello("Jaeseong")).isNull();
  }

  @Test
  void incraseCount() {
    assertThat(helloRepository.countOf("Jaeseong")).isEqualTo(0);

    helloRepository.increaseCount("Jaeseong");
    assertThat(helloRepository.countOf("Jaeseong")).isEqualTo(1);

    helloRepository.increaseCount("Jaeseong");
    assertThat(helloRepository.countOf("Jaeseong")).isEqualTo(2);
  }

}