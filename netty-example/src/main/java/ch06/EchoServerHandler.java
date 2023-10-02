package ch06;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
    ByteBuf readMessage = (ByteBuf) msg;
    System.out.println("channelRead : " + readMessage.toString(Charset.defaultCharset()));

    // ChannelHandlerContext를 통해서 네티 프레임워크에서 초기화된 ByteBufAllocator를 참조할 수 있다.
    // ByteBufAllocator는 바이트 버퍼 풀을 관리하는 인터페이스이며 플랫폼의 지원 여부에 따라 다이렉트 버퍼와 힙 버퍼 풀을 생성한다.
    // 기본적으로 다이렉트 버퍼 풀을 생성하며 애플리케이션 개발자의 필요에 따라 힙 버퍼 풀을 생성할 수도 있다.
    ByteBufAllocator byteBufAllocator = ctx.alloc();
    // ByteBufAllocator의 buffer 메서드를 사용하여 생성된 바이트 버퍼는 ByteBufAllocator의 풀에서 관리되며
    // 바이트 버퍼를 채널에 기록하거나 명시적으로 release 메서드를 호출하면 바이트 버퍼 풀로 돌아간다.
    ByteBuf newBuffer = byteBufAllocator.buffer();

    // write 메서드 인수로 바이트 버퍼가 입력되면 데이터를 채널에 기록하고 난 뒤에 버퍼로 돌아간다.
    ctx.write(msg);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
