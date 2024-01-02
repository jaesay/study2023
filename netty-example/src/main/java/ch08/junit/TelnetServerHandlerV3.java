package ch08.junit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class TelnetServerHandlerV3 extends SimpleChannelInboundHandler<String> {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    ctx.write(ResponseGenerator.makeHello());
    ctx.flush();
  }

  @Override
  public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {
    ResponseGenerator generator = new ResponseGenerator(request);
    String response = generator.response();

    ChannelFuture future = ctx.write(response);

    if (generator.isClose()) {
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
