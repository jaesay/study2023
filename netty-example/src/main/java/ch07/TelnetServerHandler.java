package ch07;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

@Sharable // 1. Sharable 어노테이션은 네티가 제공하는 공유가능 상태표시 어노테이션이다. Sharable로 지정된 클래스를 채널 파이프라인에서 공유할 수 있다는 의미이다. 즉, 다중 스레드에서 스레드 경합 없이 참조가 가능하다.
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> { // 여기에 지정된 제네릭 타입은 데이터 수신 이벤트인 channelRead0 메서드의 두 번째 인수의 데이터형이 된다. TelnetServerHandler 클래스에서는 수신된 데이터가 String 데이터임을 의미한다.

  /**
   * channelActive 메서드는 채널이 생성된 다음 호출되는 이벤트다.
   * 서버 프로그램을 예로 들면 클라이언트가 서버에 접속되면 네티의 채널이 생성되고 해당 채널이 호출된다.
   * 통상적으로 채널이 연결된 직후에 수행할 작업을 처리할 때 사용하는 이벤트다.
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.write(InetAddress.getLocalHost().getHostName() + " 서버에 접속 하셨습니다!\r\n");
    ctx.write("현재 시간은 " + new Date() + " 입니다.\r\n");
    ctx.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
    // Generate and write a response.
    String response;
    boolean close = false;
    if (request.isEmpty()) {
      response = "명령을 입력해 주세요.\r\n";
    }
    else if ("bye".equals(request.toLowerCase())) {
      response = "안녕히 가세요!\r\n";
      close = true;
    }
    else {
      response = "입력하신 명령은 '" + request + "' 입니다.\r\n";
    }

    // TelnetPipelineFactory에 지정된 StringEncoder클래스가 문자열을 버퍼로 변환해 주므로
    // ChannelBuffer에 전송할 값을 복사하여 전송하지 않고 직접 문자열을 전송한다.
    ChannelFuture future = ctx.write(response);

    // 종료 명령 플래그를 확인하여 연결된 클라이언트의 채널을 닫는다. 이때 ChannelFuture에 ChannerFutureListener.CLOSE를 등록하여 비동기로 채널을 닫는다.
    if (close) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

  /**
   * channelReadComplete 이벤트는 channelRead0 이벤트가 완료되면 호출되는 메서드이다.
   * 여기서 ChannelHandlerContext의 flush 메서드를 사용하여 채널에 기록된 데이터를 클라이언트로 즉시 전송한다.
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
