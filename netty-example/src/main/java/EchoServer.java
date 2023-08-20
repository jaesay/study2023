import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class EchoServer {

  public static void main(String[] args) throws Exception {
    // 1. EventLoopGroup 인터페이스에 NioEventLoopGroup 클래스의 객체를 할당한다. 생성자에 입력된 스레드 수가 1이므로 단일 스레드로 동작하는 NioEventLoopGroup 객체를 생성한다.
    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // 2. EventLoopGroup 인터페이스에 NioEventLoopGroup 클래스의 객체를 할당한다. 생성자에 인수가 없으므로 CPU 코어 수에 따른 스레드 수가 설정된다. 스레드 수는 하드웨어가 기지고 있는 CPU 코어 수의 2배를 사용한다. 만약 서버 애플리케이션이 동작하는 하드웨어가 4코어 CPU이고 하이퍼 스레딩을 지원한다면 16개가 생성된다.
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      // 3. ServerBootstrap을 생성한다.
      ServerBootstrap b = new ServerBootstrap();
      // 4. ServerBootstrap에 1과 2에서 생성한 NioEventLoopGroup 객체를 인수로 입력한다. 첫 번째 인수는 부모 스레드다. 부모 스레드는 클라이언트 연결 요청의 수락을 담당한다. 두 번째 인수는 연결된 소켓에 대한 I/O 처리를 담당하는 자식 스레드다.
      b.group(bossGroup, workerGroup)
          // 5. 서버 소켓(부모 스레드)이 사용할 네트워크 입출력 모드를 설정한다. 여기서는 NioServerSocketChannel 클래스를 설정했기 떄문에 NIO 모드로 동작한다.
          .channel(NioServerSocketChannel.class)
          // 6. 자식 채널의 초기화 방법을 설정한다. 여기서는 익명 클래스로 채널 초기화 방법을 지정했다.
          .childHandler(new ChannelInitializer<SocketChannel>() {
            // 7. ChannelInitializer는 클라이언트로부터 연결된 채널이 초기화할 때의 기본 동작이 지정된 추상 클래스다.
            @Override
            public void initChannel(SocketChannel ch) {
              // 8. 채널 파이프라인 객체를 생성한다.
              ChannelPipeline p = ch.pipeline();
              // 9. 채널 파이프라인에 EchoServerHandler 클래스를 등록한다. EchoServerHandler 클래스는 이후에 클라이언트의 연결이 생성되었을 때 데이터 처리를 담당한다.
              p.addLast(new EchoServerHandler());
            }
          });

      ChannelFuture f = b.bind(8888).sync();

      f.channel().closeFuture().sync();

    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

}
