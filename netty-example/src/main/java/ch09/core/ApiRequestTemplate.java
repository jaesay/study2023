package ch09.core;

import ch09.service.RequestParamException;
import ch09.service.ServiceException;
import com.google.gson.JsonObject;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ApiRequestTemplate implements ApiRequest {

  /**
   * API 요청 data
   */
  protected Map<String, String> reqData;

  /**
   * API 처리결과
   */
  protected JsonObject apiResult;

  /**
   * logger 생성<br/> apiResult 객체 생성
   */
  public ApiRequestTemplate(Map<String, String> reqData) {
    this.apiResult = new JsonObject();
    this.reqData = reqData;

    log.info("request data : " + this.reqData);
  }

  public void executeService() {
    try {
      this.requestParamValidation();
      this.service();

    } catch (RequestParamException e) {
      log.error(String.valueOf(e));
      this.apiResult.addProperty("resultCode", "405");

    } catch (ServiceException e) {
      log.error(String.valueOf(e));
      this.apiResult.addProperty("resultCode", "501");
    }
  }

  public JsonObject getApiResult() {
    return this.apiResult;
  }

  @Override
  public void requestParamValidation() throws RequestParamException {
    if (getClass().getClasses().length == 0) {
      return;
    }

    // // TODO 이건 꼼수 바꿔야 하는데..
    // for (Object item :
    // this.getClass().getClasses()[0].getEnumConstants()) {
    // RequestParam param = (RequestParam) item;
    // if (param.isMandatory() && this.reqData.get(param.toString()) ==
    // null) {
    // throw new RequestParamException(item.toString() +
    // " is not present in request param.");
    // }
    // }
  }

  public final <T extends Enum<T>> T fromValue(Class<T> paramClass, String paramValue) {
    if (paramValue == null || paramClass == null) {
      throw new IllegalArgumentException("There is no value with name '" + paramValue + " in Enum "
          + paramClass.getClass().getName());
    }

    for (T param : paramClass.getEnumConstants()) {
      if (paramValue.equals(param.toString())) {
        return param;
      }
    }

    throw new IllegalArgumentException("There is no value with name '" + paramValue + " in Enum "
        + paramClass.getClass().getName());
  }
}
