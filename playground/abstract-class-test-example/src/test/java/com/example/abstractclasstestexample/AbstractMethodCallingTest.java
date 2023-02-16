package com.example.abstractclasstestexample;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willCallRealMethod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@DisplayName("Non-Abstract Method (e.g. 템플릿 메소드)에서 Abstract Method 호출할 떄 테스트")
class AbstractMethodCallingTest {
  private AbstractMethodCalling abstractMethodCalling;

  @BeforeEach
  void setUp() {
    abstractMethodCalling = Mockito.mock(AbstractMethodCalling.class);
  }

  @Test
  void test() {
    given(abstractMethodCalling.abstractFunc()).willReturn("Abstract");
    willCallRealMethod().given(abstractMethodCalling).defaultImpl();

    assertThat(abstractMethodCalling.defaultImpl()).isEqualTo("Abstract Default");
  }

  @Test
  void test2() {
    given(abstractMethodCalling.abstractFunc()).willReturn(null);
    willCallRealMethod().given(abstractMethodCalling).defaultImpl();

    assertThat(abstractMethodCalling.defaultImpl()).isEqualTo("Default");
  }
}