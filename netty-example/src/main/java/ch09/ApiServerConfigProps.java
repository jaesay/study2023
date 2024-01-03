package ch09;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ch09")
@PropertySource("classpath:api-server.properties")
@Getter
public class ApiServerConfigProps {

  private final int bossThreadCount;
  private final int workerThreadCount;
  private final int serverPort;

  public ApiServerConfigProps(
      @Value("${boss.thread.count}") int bossThreadCount,
      @Value("${worker.thread.count}") int workerThreadCount,
      @Value("${server.port}") int serverPort) {

    this.bossThreadCount = bossThreadCount;
    this.workerThreadCount = workerThreadCount;
    this.serverPort = serverPort;
  }
}
