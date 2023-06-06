package com.example.redispubsub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {

  private final RedisTemplate<String, String> eventRedisTemplate;
  private final ChannelTopic eventTopic;

  public void sendMessage(EventMessage eventMessage) {
    eventRedisTemplate.convertAndSend(eventTopic.getTopic(), eventMessage);
  }
}
