package com.example.springevent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter @ToString
@RequiredArgsConstructor
public class HotelCreateEvent {
  private final Long hotelId;
  private final String hotelAddress;

  public static HotelCreateEvent of(Long hotelId, String hotelAddress) {
    return new HotelCreateEvent(hotelId, hotelAddress);
  }
}
