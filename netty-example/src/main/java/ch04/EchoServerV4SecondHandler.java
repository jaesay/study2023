package ch04;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;

public class EchoServerV4SecondHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf readMessage = (ByteBuf) msg;
    System.out.println(
        "SecondHandler channelRead : " + readMessage.toString(Charset.defaultCharset()));
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) {
    System.out.println("channelReadComplete 발생");
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
