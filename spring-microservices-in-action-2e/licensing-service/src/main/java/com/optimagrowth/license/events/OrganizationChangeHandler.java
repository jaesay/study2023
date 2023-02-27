package com.optimagrowth.license.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
@Slf4j
public class OrganizationChangeHandler {

  /**
   * 데이터가 진행하는 액션을 확인하고 적절히 반응한다.
   * @param organization
   */
  @StreamListener("inboundOrgChanges")
  public void logSink(OrganizationChangeModel organization) {

    log.debug("Received a message of type " + organization.getType());

    switch(organization.getAction()){
      case "GET":
        log.debug("Received a GET event from the organization service for organization id {}", organization.getOrganizationId());
        break;
      case "SAVE":
        log.debug("Received a SAVE event from the organization service for organization id {}", organization.getOrganizationId());
        break;
      case "UPDATE":
        log.debug("Received a UPDATE event from the organization service for organization id {}", organization.getOrganizationId());
        break;
      case "DELETE":
        log.debug("Received a DELETE event from the organization service for organization id {}", organization.getOrganizationId());
        break;
      default:
        log.error("Received an UNKNOWN event from the organization service of type {}", organization.getType());
        break;
    }
  }
}
