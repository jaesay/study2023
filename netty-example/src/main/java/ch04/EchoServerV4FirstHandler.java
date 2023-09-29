package ch04;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;

public class EchoServerV4FirstHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf readMessage = (ByteBuf) msg;
    System.out.println(
        "FirstHandler channelRead : " + readMessage.toString(Charset.defaultCharset()));
    ctx.write(msg);
    ctx.fireChannelRead(msg);
  }
}
