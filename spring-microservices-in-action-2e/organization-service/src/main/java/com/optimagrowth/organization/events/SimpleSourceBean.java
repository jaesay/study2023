package com.optimagrowth.organization.events;

import com.optimagrowth.organization.utils.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SimpleSourceBean {

  private final Source source;

  public void publishOrganizationChange(String action, String organizationId){
    log.debug("Sending Kafka message {} for Organization Id: {}", action, organizationId);

    // POJO 메시지를 발행한다.
    OrganizationChangeModel change =  new OrganizationChangeModel(
        OrganizationChangeModel.class.getTypeName(),
        action,
        organizationId,
        UserContextHolder.getContext().getCorrelationId());

    // Source 클래스에서 정의된 채널에서 전달된 메시지를 발송한다.
    source.output().send(MessageBuilder.withPayload(change).build());
  }
}
