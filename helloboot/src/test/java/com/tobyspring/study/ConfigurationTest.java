package com.tobyspring.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@DisplayName("@Configuration 동작을 학습하기 위한 테스트")
class ConfigurationTest {

  @Test
  @DisplayName("Configuration 클래스는 기본적으로 프록시(proxyBeanMethods=true)를 만들어서 "
      + "팩토리 메소드를 사용하여 오브젝트를 생성하면 한 개의 오브젝트만 사용하도록 한다.")
  void configuration() {
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
    applicationContext.register(MyConfig.class);
    applicationContext.refresh();

    MyConfig myConfig = applicationContext.getBean(MyConfig.class);
    Bean1 bean1 = applicationContext.getBean(Bean1.class);
    Bean2 bean2 = applicationContext.getBean(Bean2.class);

    System.out.println(myConfig.getClass());
    Assertions.assertThat(bean1.common).isSameAs(bean2.common);
  }

  @Test
  void proxyCommonMethod() {
    MyConfigProxy myConfigProxy = new MyConfigProxy();

    Bean1 bean1 = myConfigProxy.bean1();
    Bean2 bean2 = myConfigProxy.bean2();

    Assertions.assertThat(bean1.common).isSameAs(bean2.common);
  }

  static class MyConfigProxy extends MyConfig {
    private Common common;

    @Override
    Common common() {
      if (this.common == null) {
        this.common = super.common();
      }

      return this.common;
    }
  }

//  @Configuration(proxyBeanMethods = false)
@Configuration
  static class MyConfig {
    @Bean
    Common common() {
      return new Common();
    }

    @Bean
    Bean1 bean1() {
      return new Bean1(common());
    }

    @Bean
    Bean2 bean2() {
      return new Bean2(common());
    }
  }


  static class Bean1 {
    private final Common common;

    Bean1(Common common) {
      this.common = common;
    }
  }

  static class Bean2 {
    private final Common common;

    Bean2(Common common) {
      this.common = common;
    }
  }

  static class Common {
  }
}
