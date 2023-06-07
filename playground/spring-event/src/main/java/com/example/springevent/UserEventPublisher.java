package com.example.springevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventPublisher {

  private final ApplicationEventPublisher applicationEventPublisher;

  public void publishUserCreated(Long userId, String emailAddress) {
    UserEvent userEvent = UserEvent.created(this, userId, emailAddress);
    log.info("Publish user created event.");
    applicationEventPublisher.publishEvent(userEvent);
  }
}
