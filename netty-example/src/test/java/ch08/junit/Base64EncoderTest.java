package ch08.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.base64.Base64Encoder;
import java.nio.charset.Charset;
import org.junit.jupiter.api.Test;

class Base64EncoderTest {

  @Test
  void testEncoder() {
    String writeData = "안녕하세요";
    ByteBuf request = Unpooled.wrappedBuffer(writeData.getBytes());

    Base64Encoder encoder = new Base64Encoder();
    EmbeddedChannel embeddedChannel = new EmbeddedChannel(encoder);

    embeddedChannel.writeOutbound(request);
    ByteBuf response = embeddedChannel.readOutbound();

    String expect = "7JWI64WV7ZWY7IS47JqU";
    assertEquals(expect, response.toString(Charset.defaultCharset()));

    embeddedChannel.finish();
  }
}
