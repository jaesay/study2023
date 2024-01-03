package ch09.service;

import ch09.core.ApiRequestTemplate;
import java.util.Map;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service("users")
@Scope("prototype")
public class UserInfo extends ApiRequestTemplate {

  private static final Map<String, String> USER_MAP = Map.of("user1@email.com", "1");

  public UserInfo(Map<String, String> reqData) {
    super(reqData);
  }

  @Override
  public void requestParamValidation() throws RequestParamException {
    if (!StringUtils.hasLength(this.reqData.get("email"))) {
      throw new RequestParamException("email이 없습니다.");
    }
  }

  @Override
  public void service() throws ServiceException {
    if (USER_MAP.containsKey(this.reqData.get("email"))) {
      this.apiResult.addProperty("resultCode", "200");
      this.apiResult.addProperty("message", "Success");
      this.apiResult.addProperty("userNo", USER_MAP.get(this.reqData.get("email")));

    } else {
      this.apiResult.addProperty("resultCode", "404");
    }
  }
}
