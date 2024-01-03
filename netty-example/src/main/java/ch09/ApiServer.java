package ch09;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 서버 부트스트랩 설정
 */
@Component
@RequiredArgsConstructor
public class ApiServer {

  private final ApiServerConfigProps props;

  public void start() {
    EventLoopGroup bossGroup = new NioEventLoopGroup(props.getBossThreadCount());
    EventLoopGroup workerGroup = new NioEventLoopGroup(props.getWorkerThreadCount());
    ChannelFuture channelFuture = null;

    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new ApiServerInitializer(null));

      Channel ch = b.bind(props.getServerPort()).sync().channel();

      channelFuture = ch.closeFuture();
      // 서버 채널의 closeFuture 객체를 가져와서 채널 닫힘 이벤트가 발생할 때까지 대기한다. 즉, 서버의 메인 스레드는 여기서 멈춘다.
      channelFuture.sync();

    } catch (InterruptedException e) {
      e.printStackTrace();

    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }
}
