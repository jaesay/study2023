package com.tobyspring.study;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

@DisplayName("@Conditional 동작을 학습하기 위한 테스트")
public class ConditionalTest {

  @Test
  @DisplayName("Condition이 true이면 빈으로 등록되고 false이면 등록되지 않는다.")
  void conditional() {
    // true
    new ApplicationContextRunner().withUserConfiguration(Config1.class)
        .run(context -> {
          assertThat(context).hasSingleBean(MyBean.class);
          assertThat(context).hasSingleBean(Config1.class);
        });

    // false
    new ApplicationContextRunner().withUserConfiguration(Config2.class)
        .run(context -> {
          assertThat(context).doesNotHaveBean(MyBean.class);
          assertThat(context).doesNotHaveBean(Config1.class);
        });
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @Conditional(BooleanCondition.class)
  @interface BooleanConditional {
    boolean value();
  }

  @Configuration
  @BooleanConditional(true)
  static class Config1 {
    @Bean
    MyBean myBean() {
      return new MyBean();
    }
  }

  @Configuration
  @BooleanConditional(false)
  static class Config2 {
    @Bean
    MyBean myBean() {
      return new MyBean();
    }
  }

  static class MyBean {}

  static class BooleanCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
      Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(BooleanConditional.class.getName());
      Boolean value = (Boolean) annotationAttributes.get("value");
      return value;
    }
  }
}
