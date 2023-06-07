package com.example.springevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserEventListener implements ApplicationListener<UserEvent> {

  private final EventService eventService;

  @Override
  @Async
  public void onApplicationEvent(UserEvent event) {
    if (UserEvent.Type.CREATE == event.getType()) {
      log.info("Listen CREATE event. {}, {}", event.getUserId(), event.getEmailAddress());
      eventService.sendEventMail(event.getEmailAddress());

    } else if (UserEvent.Type.DELETE == event.getType()) {
      log.info("Listen DELETE event");

    } else {
      log.error("Unsupported event type. {}", event.getType());
    }
  }
}
