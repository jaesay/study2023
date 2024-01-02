package ch08.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import io.netty.channel.embedded.EmbeddedChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import org.junit.jupiter.api.Test;

class TelnetServerHandlerV3Test {

  @Test
  void testConnect() {
    StringBuilder builder = new StringBuilder();
    try {
      builder.append("환영합니다. ")
          .append(InetAddress.getLocalHost().getHostName())
          .append("에 접속하셨습니다!\r\n")
          .append("현재 시간은 ")
          .append(new Date())
          .append(" 입니다.\r\n");

    } catch (UnknownHostException e) {
      fail();
      e.printStackTrace();
    }

    EmbeddedChannel embeddedChannel = new EmbeddedChannel(new TelnetServerHandlerV3());

    // channelActive 확인
    // 인바운드 이벤트 헨들러의 channelActive 이벤트 메서드는 이벤트 핸들러가 EmbeddedChannel에 등록될 때 호출된다.
    // 그러므로 다른 write 이벤트 메서드 호출 없이 readOutbound 메서드로 아웃바운드 데이터를 조회할 수 있다.
    String expected = embeddedChannel.readOutbound();
    assertNotNull(expected);

    assertEquals(builder.toString(), expected);

    // channelRead0, channelReadComplete 확인
    String request = "hello";
    expected = "입력하신 명령이 '" + request + "' 입니까?\r\n";

    embeddedChannel.writeInbound(request);

    String response = embeddedChannel.readOutbound();
    assertEquals(expected, response);

    embeddedChannel.finish();
  }
}