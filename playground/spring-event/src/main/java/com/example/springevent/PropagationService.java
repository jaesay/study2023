package com.example.springevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PropagationService {

  public void propagateHotelEvent() {
    log.info("propagation of hotel event");
  }

  public void propagateResourceEvent() {
    log.info("propagation of resource event");
  }
}
