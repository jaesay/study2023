package com.example.kafkaproducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/publisher")
@RequiredArgsConstructor
@Slf4j
public class EventController {

  private final KafkaMessagePublisher publisher;

  @GetMapping("/publish/{message}")
  public ResponseEntity<?> publishMessage(@PathVariable String message) {

    try {
      for (int i = 0; i < 10_000; i++) {
        publisher.sendMessageToTopic(message + " : " + i);
      }
      return ResponseEntity.ok("message published successfully..");

    } catch (Exception ex) {
      log.error(ex.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
