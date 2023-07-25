package com.example.kafkaproducer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaProducerConfig {

  @Bean
  public NewTopic newTopic() {
    return new NewTopic("topic-demo", 3, (short) 1);
  }
}
