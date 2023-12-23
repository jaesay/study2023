package hello.core.beandefinition;

import hello.core.AppConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class BeanDefinitionTest {

  AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class); // 팩토리 메서드를 통해..
//  GenericXmlApplicationContext ac = new GenericXmlApplicationContext("appConfig.xml"); // 컨테이너에 직접 등록..

  @Test
  @DisplayName(" 빈 설정 메타정보 확인")
  void findApplicationBean() {
    String[] beanDefinitionNames = ac.getBeanDefinitionNames();
    for (String beanDefinitionName : beanDefinitionNames) {
      BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
      if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION) {
        System.out.println("beanDefinitionName" + beanDefinitionName +
            " beanDefinition = " + beanDefinition);
      }
    }
  }

}
