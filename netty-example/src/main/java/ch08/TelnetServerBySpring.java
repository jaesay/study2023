package ch08;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class TelnetServerBySpring {

  public static void main(String[] args) {
    AbstractApplicationContext springContext = null;
    try {
      springContext = new AnnotationConfigApplicationContext(TelnetServerConfig.class);
      springContext.registerShutdownHook();

      TelnetServerV2 server = springContext.getBean(TelnetServerV2.class);
      server.start();
    } finally {
      springContext.close();
    }
  }
}
