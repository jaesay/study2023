package com.example.springevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserEventPublisher userEventPublisher;

  public Boolean createUser(String userName, String emailAddress) {
    log.info("created user. {}, {}", userName, emailAddress);
    userEventPublisher.publishUserCreated(1239876L, emailAddress);
    log.info("done create user");
    return Boolean.TRUE;
  }
}
