package com.example.kafkaproducer;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaMessagePublisher {

  private final KafkaTemplate<String, String> template;

  public void sendMessageToTopic(String message) {
    CompletableFuture<SendResult<String, String>> future = template.send("topic-demo", message);
    future.whenComplete((result, ex) -> {
      if (ex == null) {
        log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());

      } else {
        log.info("Unable to send message=[{}] due to : {}", message, ex.getMessage());
      }
    });
  }

}
