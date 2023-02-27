package com.optimagrowth.license.events;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CustomChannels {

  @Input("inboundOrgChanges") // 채널 이름을 지정한다.
  SubscribableChannel orgs(); // @Input 애노테이션으로 노출된 채널에 대한 SubscribableChannel 클래스를 반환한다.
}
