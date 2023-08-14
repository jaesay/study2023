import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;
import java.nio.charset.Charset;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    String sendMessage = "Hello netty";
    ByteBuf messageBuffer = Unpooled.buffer();
    messageBuffer.writeBytes(sendMessage.getBytes());
    System.out.println("전송한 문자열 [" + sendMessage + "]");
    ctx.writeAndFlush(messageBuffer);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    String readMessage = ((ByteBuf) msg).toString(Charset.defaultCharset());
    System.out.println("수신한 문자열 [" + readMessage + "]");
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    ctx.close();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
