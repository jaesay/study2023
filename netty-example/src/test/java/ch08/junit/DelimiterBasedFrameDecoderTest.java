package ch08.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

class DelimiterBasedFrameDecoderTest {

  @Test
  void testDecoder() {
    String writeData = "안녕하세요\r\n반갑습니다\r\n";
    String firstResponse = "안녕하세요\r\n";
    String secondResponse = "반갑습니다\r\n";

    DelimiterBasedFrameDecoder decoder = new DelimiterBasedFrameDecoder(8192, false,
        Delimiters.lineDelimiter());
    EmbeddedChannel embeddedChannel = new EmbeddedChannel(decoder);

    ByteBuf request = Unpooled.wrappedBuffer(writeData.getBytes());
    // EmbeddedChannel의 writeInbound 메서드로 데이터를 기록하면 EmbeddedChannel에 등록된 인바운드 이벤트 핸들러인 디코더를 거쳐서 데이터가 디코딩된다.
    boolean result = embeddedChannel.writeInbound(request);
    assertTrue(result);

    ByteBuf response = null;

    // readInbound 메서드로 디코더가 디코딩한 데이터를 얻을 수 있다.
    response = embeddedChannel.readInbound();
    assertEquals(firstResponse, response.toString(Charset.defaultCharset()));

    response = embeddedChannel.readInbound();
    assertEquals(secondResponse, response.toString(Charset.defaultCharset()));

    embeddedChannel.finish();
  }
}
