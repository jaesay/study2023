package com.example.springevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelService {

  private final HotelEventPublisher hotelEventPublisher;

  @Transactional
  public Boolean createHotel(String hotelName, String hotelAddress) {
    log.info("created hotel. {}, {}", hotelName, hotelAddress);
    hotelEventPublisher.publishHotelCreated(999111222L, hotelAddress);
    log.info("done create hotel");
    return Boolean.TRUE;
  }
}
