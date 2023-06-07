package com.example.springevent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class HotelServiceTest {

  @Autowired
  HotelService hotelService;

  @Test
  void createHotel() throws InterruptedException {
    hotelService.createHotel("test", "test address");
    Thread.sleep(3000);
  }
}