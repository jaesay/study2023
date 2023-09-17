package com.example.hexarch.member.adapter.in.web;

import com.example.hexarch.common.WebAdapter;
import com.example.hexarch.member.application.port.in.GetMemberQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@WebAdapter
@RequiredArgsConstructor
class GetMemberHandler {

  private final GetMemberQuery query;

  public Mono<ServerResponse> getMember(ServerRequest request) {

    return null;
  }
}
