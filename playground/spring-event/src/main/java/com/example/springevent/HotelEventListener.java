package com.example.springevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class HotelEventListener {

  private final PropagationService propagationService;

  @Async
  @Order(1)
  @EventListener(value = HotelCreateEvent.class)
//  @TransactionalEventListener(classes = HotelCreateEvent.class)
  public void handleHotelCreateEvent(HotelCreateEvent hotelCreateEvent) {
    log.info("handle HotelCreatedEvent : {}", hotelCreateEvent);
    propagationService.propagateHotelEvent();
  }

  @Async
  @Order(2)
  @EventListener(value = HotelCreateEvent.class)
  public void handleResourceCreateEvent(HotelCreateEvent hotelCreateEvent) {
    log.info("handle resourceCreatedEvent : {}", hotelCreateEvent);
    propagationService.propagateResourceEvent();
  }
}
