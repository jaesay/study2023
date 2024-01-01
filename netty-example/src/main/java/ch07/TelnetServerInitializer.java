package ch07;

import io.netty.channel.ChannelHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {

  private static final StringDecoder DECODER = new StringDecoder(CharsetUtil.UTF_8);
  private static final StringEncoder ENCODER = new StringEncoder(CharsetUtil.UTF_8);

  private static final TelnetServerHandler SERVER_HANDLER = new TelnetServerHandler();

  @Override
  public void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    // Add the text line codec combination first,
    pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
    // 새로운 클라이언트 채널들이 동일한 인코더/디코더 객체를 공유하게 된다.
    pipeline.addLast(DECODER);
    pipeline.addLast(ENCODER);

    // 위 인코더/디코더와 마찬가지로 모든 채널 파이프라인에서 공유된다.
    pipeline.addLast(SERVER_HANDLER);
  }
}
