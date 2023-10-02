package ch06;

import static org.assertj.core.api.Assertions.assertThat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.nio.charset.Charset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings({"NonAsciiCharacters"})
@DisplayName("자바 NIO 바이트 버퍼 테스트")
class NettyByteBufferTest {

  @Test
  void 생성_테스트() {
    ByteBuf buf = Unpooled.buffer(11);
    createBufferTest(buf, false);

    ByteBuf buf2 = Unpooled.directBuffer(11);
    createBufferTest(buf2, true);

    ByteBuf buf3 = PooledByteBufAllocator.DEFAULT.heapBuffer(11);
    createBufferTest(buf3, false);

    ByteBuf buf4 = PooledByteBufAllocator.DEFAULT.directBuffer(11);
    createBufferTest(buf4, true);
  }

  private void createBufferTest(ByteBuf buf, boolean isDirect) {
    assertThat(buf.capacity()).isEqualTo(11);
    assertThat(buf.isDirect()).isEqualTo(isDirect);

    assertThat(buf.readableBytes()).isEqualTo(0);
    assertThat(buf.writableBytes()).isEqualTo(11);
  }

  @Test
  void 읽기_쓰기_테스트() {
    ByteBuf buf = Unpooled.buffer(11);
    readWriteByteBufferTest(buf, false);

    ByteBuf buf2 = Unpooled.directBuffer(11);
    readWriteByteBufferTest(buf2, true);

    ByteBuf buf3 = PooledByteBufAllocator.DEFAULT.heapBuffer(11);
    readWriteByteBufferTest(buf3, false);

    ByteBuf buf4 = PooledByteBufAllocator.DEFAULT.directBuffer(11);
    readWriteByteBufferTest(buf4, true);
  }

  private void readWriteByteBufferTest(ByteBuf buf, boolean isDirect) {
    assertThat(buf.capacity()).isEqualTo(11);
    assertThat(buf.isDirect()).isEqualTo(isDirect);

    assertThat(buf.readableBytes()).isEqualTo(0);
    assertThat(buf.writableBytes()).isEqualTo(11);

    buf.writeInt(65537);
    assertThat(buf.readableBytes()).isEqualTo(4);
    assertThat(buf.writableBytes()).isEqualTo(7);

    assertThat(buf.readShort()).isEqualTo((short) 1);
    assertThat(buf.readableBytes()).isEqualTo(2);
    assertThat(buf.writableBytes()).isEqualTo(7);

    assertThat(buf.isReadable()).isTrue();

    buf.clear();

    assertThat(buf.readableBytes()).isEqualTo(0);
    assertThat(buf.writableBytes()).isEqualTo(11);
  }

  @Test
  void 가변_크기_버퍼_테스트() {
    ByteBuf buf = Unpooled.buffer(11);
    dynamicByteBufferTest(buf, false);

    ByteBuf buf2 = Unpooled.directBuffer(11);
    dynamicByteBufferTest(buf2, true);

    ByteBuf buf3 = PooledByteBufAllocator.DEFAULT.heapBuffer(11);
    dynamicByteBufferTest(buf3, false);

    ByteBuf buf4 = PooledByteBufAllocator.DEFAULT.directBuffer(11);
    dynamicByteBufferTest(buf4, true);
  }

  private void dynamicByteBufferTest(ByteBuf buf, boolean isDirect) {
    assertThat(buf.capacity()).isEqualTo(11);
    assertThat(buf.isDirect()).isEqualTo(isDirect);

    String sourceData = "hello world";
    buf.writeBytes(sourceData.getBytes());
    assertThat(buf.readableBytes()).isEqualTo(11);
    assertThat(buf.writableBytes()).isEqualTo(0);

    assertThat(buf.toString(Charset.defaultCharset())).isEqualTo(sourceData);

    buf.capacity(6);
    assertThat(buf.toString(Charset.defaultCharset())).isEqualTo("hello ");
    assertThat(buf.capacity()).isEqualTo(6);

    buf.capacity(13);
    assertThat(buf.toString(Charset.defaultCharset())).isEqualTo("hello ");

    buf.writeBytes("world".getBytes());
    assertThat(buf.toString(Charset.defaultCharset())).isEqualTo(sourceData);

    assertThat(buf.capacity()).isEqualTo(13);
    assertThat(buf.writableBytes()).isEqualTo(2);
  }
}
