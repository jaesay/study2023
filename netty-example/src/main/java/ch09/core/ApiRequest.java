package ch09.core;

import com.google.gson.JsonObject;

public interface ApiRequest {

  /**
   * API를 호출하는 HTTP 요청의 파라미터 값이 입력되었는지 검증하는 메서드
   */
  void requestParamValidation();

  /**
   * 각 API 서비스에 따른 개별 구현 메서드
   */
  void service();

  /**
   * 서비스 API의 호출 시작 메서드
   */
  void executeService();

  /**
   * API 서비스의 처리 결과를 조회하는 메서드
   */
  JsonObject getApiResult();
}
