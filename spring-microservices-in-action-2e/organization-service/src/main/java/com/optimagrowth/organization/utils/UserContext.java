package com.optimagrowth.organization.utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContext {

  public static final String CORRELATION_ID = "tmx-correlation-id";
  public static final String AUTH_TOKEN = "Authencation";
  public static final String USER_ID = "tmx-user-id";
  public static final String ORGANIZATION_ID = "tmx-organization-id";

  private String correlationId = "";
  private String authToken = "";
  private String userId = "";
  private String organizationId = ""; // 변수를 ThreadLocal로 저장하면 현재 쓰레드에 대한 데이터를 쓰레드 별로 저장할 수 있다. 여기에 설정된 정보는 그 값을 설정한 쓰레드만 읽을 수 있다.
}
