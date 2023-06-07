package com.example.springevent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventService {
  public void sendEventMail(String emailAddress) {
    log.info("Send Email attached welcome coupons. {}", emailAddress);
  }
}
