package com.example.hexarch.member.adapter.in.web;

import com.example.hexarch.common.WebAdapter;
import com.example.hexarch.member.application.port.in.RegisterMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@WebAdapter
@RequiredArgsConstructor
class RegisterMemberHandler {

  private final RegisterMemberUseCase useCase;

  Mono<ServerResponse> registerMember(ServerRequest request) {
    // TODO HTTP 요청을 자바 객체로 매핑
    // TODO 권한 검사
    // TODO 입력 유효성 검증
    // TODO 입력을 유스케이스의 입력 모델로 매핑
    // TODO 유스케이스 호출
    // TODO 유스케이스의 출력을 HTTP로 매핑
    // TODO HTTP 응답을 반환

    return null;
  }
}
