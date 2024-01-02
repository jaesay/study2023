package ch08;

import java.net.InetSocketAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("ch08")
@PropertySource("classpath:telnet-server.properties")
public class TelnetServerConfig {

  @Value("${boss.thread.count}")
  private int bossCount;

  @Value("${worker.thread.count}")
  private int workerCount;

  @Value("${tcp.port}")
  private int tcpPort;

  @Bean(name = "tcpSocketAddress")
  public InetSocketAddress tcpPort() {
    return new InetSocketAddress(tcpPort);
  }
}
