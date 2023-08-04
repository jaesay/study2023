package com.example.restassured;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/members")
public class MemberController {

  @RequestMapping("/{memberId}")
  public Mono<ResponseEntity<MemberDto>> getMember(@PathVariable long memberId) {
    return Mono.just(ResponseEntity.ok(new MemberDto(memberId, "member1")));
  }

  @GetMapping("")
  public Mono<ResponseEntity<List<MemberDto>>> getMembers() {
    return Mono.just(
        ResponseEntity.ok(
            List.of(
                new MemberDto(1L, "member1"),
                new MemberDto(2L, "member2"))
        )
    );
  }

  @PostMapping("")
  public Mono<ResponseEntity<MemberDto>> createMember(@RequestBody CreateMemberRequest request) {
    return Mono.just(ResponseEntity.ok(new MemberDto(1L, request.getName())));
  }
}
