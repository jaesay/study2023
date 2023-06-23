package com.example.webfluxpatterns._04_orchestrator_seq.util;

import com.example.webfluxpatterns._04_orchestrator_seq.dto.OrchestrationRequestContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DebugUtil {

  public static void print(OrchestrationRequestContext ctx) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(ctx));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
  }
}
