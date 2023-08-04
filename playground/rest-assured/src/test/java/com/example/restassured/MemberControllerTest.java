package com.example.restassured;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.http.MediaType;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings({"NonAsciiCharacters"})
class MemberControllerTest {

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.port = port;
  }

  @Test
  void 회원_조회() {
    MemberDto memberDto = RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/v1/members/{memberId}", 1)
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK)
        .extract()
        .as(MemberDto.class);

    assertThat(memberDto).isNotNull();
    assertThat(memberDto.getId()).isEqualTo(1L);
    assertThat(memberDto.getName()).isEqualTo("member1");
  }

  @Test
  void 회원_조회2() {
    RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/v1/members/{memberId}", 1)
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK)
        .body("id", equalTo(1))
        .body("name", notNullValue());
  }

  @Test
  void 회원_목록_조회() {
    List<MemberDto> memberDtos = RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/v1/members")
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK)
        .extract()
        .as(new TypeRef<>() {});

    assertThat(memberDtos).isNotNull();
    assertThat(memberDtos).hasSize(2);
    assertThat(memberDtos)
        .extracting("id")
        .containsExactly(1L, 2L);
  }

  @Test
  void 회원_목록_조회2() {
    RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
        .get("/v1/members")
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK)
        .body("findAll{it}.get(0).id", equalTo(1))
        .body("findAll{it}.get(0).name", equalTo("member1"))
        .body("findAll{it}.get(1).id", equalTo(2))
        .body("findAll{it}.get(1).name", equalTo("member2"));
  }

  @Test
  void 회원_등록() {
    MemberDto memberDto = RestAssured
        .given()
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .body(new CreateMemberRequest("member1"))
        .post("/v1/members")
        .then()
        .assertThat()
        .statusCode(HttpStatus.OK)
        .extract()
        .as(MemberDto.class);

    assertThat(memberDto).isNotNull();
    assertThat(memberDto.getId()).isEqualTo(1L);
    assertThat(memberDto.getName()).isEqualTo("member1");
  }

}