package com.tobyspring.helloboot;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class HellobootApplication {

  public static void main(String[] args) {
    /* spring container 생성 */
    GenericWebApplicationContext applicationContext = new GenericWebApplicationContext() {

      /* template method pattern */
      @Override
      protected void onRefresh() {
        super.onRefresh();

        ServletWebServerFactory serverFactory = new TomcatServletWebServerFactory();

        /* servlet container 생성 */
        WebServer webServer = serverFactory.getWebServer(servletContext -> {
          servletContext.addServlet("dispatcherServlet", new DispatcherServlet(this))
              .addMapping("/*");
        });
        webServer.start();
      }
    };
    /* 구성정보 */
    applicationContext.registerBean(HelloController.class);
    applicationContext.registerBean(SimpleHelloService.class);
    /* 빈 초기화 */
    applicationContext.refresh();
  }

}
