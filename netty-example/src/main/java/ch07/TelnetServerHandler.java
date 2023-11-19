package ch07;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;

@Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {
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

    // 클라이언트로부터 bye 명령을 수신하면 close 플래그가 true값이 되므로 연결을 끊는다.
    if (close) {
      future.addListener(ChannelFutureListener.CLOSE);
    }
  }

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
