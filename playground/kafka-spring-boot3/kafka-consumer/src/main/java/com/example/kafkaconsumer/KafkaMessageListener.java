package com.example.kafkaconsumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaMessageListener {

  @KafkaListener(topics = "topic-demo", groupId = "demo-group")
  public void consume1(String message) {
    log.info("consumer1 consumes the message {} ", message);
  }

  @KafkaListener(topics = "topic-demo", groupId = "demo-group")
  public void consume2(String message) {
    log.info("consumer2 consumes the message {} ", message);
  }

  @KafkaListener(topics = "topic-demo", groupId = "demo-group")
  public void consume3(String message) {
    log.info("consumer3 consumes the message {} ", message);
  }

  @KafkaListener(topics = "topic-demo", groupId = "demo-group")
  public void consume4(String message) {
    log.info("consumer4 consumes the message {} ", message);
  }
}
