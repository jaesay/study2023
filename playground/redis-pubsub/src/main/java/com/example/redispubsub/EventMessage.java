package com.example.redispubsub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EventMessage {
  private final Long timestamp;
  private final String message;

  public EventMessage(String message) {
    this.timestamp = System.currentTimeMillis();
    this.message = message;
  }

  @JsonCreator
  public EventMessage(@JsonProperty("timestamp") Long timestamp,
                      @JsonProperty("message") String message) {

    this.timestamp = timestamp;
    this.message = message;
  }
}
