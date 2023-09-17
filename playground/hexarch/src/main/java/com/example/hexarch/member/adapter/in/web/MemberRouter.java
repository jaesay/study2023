package com.example.hexarch.member.adapter.in.web;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
class MemberRouter {

  private final GetMemberHandler getMemberHandler;
  private final RegisterMemberHandler registerMemberHandler;

  @Bean
  RouterFunction<ServerResponse> routeMember() {
    return route()
        .GET("/members/{memberId}", getMemberHandler::getMember)
        .POST("/members", registerMemberHandler::registerMember)
        .build();
  }
}
