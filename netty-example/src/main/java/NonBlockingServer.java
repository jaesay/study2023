import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NonBlockingServer {

  private final Map<SocketChannel, List<byte[]>> keepDataTrack = new HashMap<>();
  private final ByteBuffer buffer = ByteBuffer.allocate(2 * 1024);

  private void startEchoServer() {
    try (
        // 2. 자바 NIO 컴포넌트 중의 하나인 Selector는 자신에게 등록된 채널에 변경 사항이 발생했는지 검사하고 변경 사항이 발생한 채널에 대한 접근을 가능하게 해준다. 여기서는 새로운 Selector 객체를 생성했다.
        Selector selector = Selector.open();
        // 3. 블로킹 소켓의 ServerSocket에 대응되는 논블로킹 소켓의 서버 소켓 채널을 생성한다. 블로킹 소켓과 다르게 소켓 채널을 먼저 생성하고 사용할 포트를 바인딩한다.
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()
    ) {

      // 4. 생성한 Selector와 ServerSocketChannel 객체가 정상적으로 생성되었는지 확인한다.
      if ((serverSocketChannel.isOpen()) && (selector.isOpen())) {
        // 5. 소켓 채널의 블로킹 모드의 기본값은 true다. 즉, 별도로 논블로킹 모드로 지정하지 않으면 블로킹 모드로 동작한다. 여기서는 ServerSocketChannel 객체를 논블로킹 모드로 설정했다.
        serverSocketChannel.configureBlocking(false);
        // 6. 클라이언트의 연결을 대기할 포트를 지정하고 생성된 ServerSocketChannel 객체에 할당한다. 이 작업이 완료되면 ServerSocketChannel 객체가 지정된 포트로부터 클라이언트의 연결을 생성할 수 있다.
        serverSocketChannel.bind(new InetSocketAddress(8888));

        // 7. ServerSocketChannel 객체를 Selector 객체에 등록한다. Selector가 감지할 이벤트는 연결 요청에 해당하는 SelectionKey.OP_ACCEPT 다.
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("접속 대기중");

        while (true) {
          // 8. Selector에 등록된 채널에서 변경 사항이 발생했는지 검사한다. Selector에 아무런 I/O 이벤트도 발생하지 않으면 스레드는 이 부분에서 블로킹된다. I/O 이벤트가 발생하지 않을 때 블로킹을 피하고 싶다면 selectNow 메서드를 사용하면 된다.
          selector.select();
          // 9. Selector에 등록된 채널 중에서 I/O 이벤트가 발생한 채널들의 목록을 조회한다.
          Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

          while (keys.hasNext()) {
            SelectionKey key = keys.next();
            // 10. I/O 이벤트가 발생한 채널에서 동일한 이벤트가 감지되는 것을 방지하기 위하여 조회된 목록에서 제거한다.
            keys.remove();

            if (!key.isValid()) {
              continue;
            }

            // 11. 조회된 I/O 이벤트의 종류가 연결 요청인지 확인한다. 만약 연결 요청 이벤트라면 연결처리 메서드로 이동한다.
            if (key.isAcceptable()) {
              this.acceptOP(key, selector);

            // 12. 조회된 I/O 이벤트의 종류가 데이터 수신인지 확인한다. 만약 데이터 수신 이벤트라면 데이터 읽기 처리 메서드로 이동한다.
            } else if (key.isReadable()) {
              this.readOP(key);

            // 13. 조회된 I/O 이벤트의 종류가 데이터 쓰기 가능인지 확인한다. 만약 데이터 쓰기 가능 이벤트라면 데이터 쓰기 처리 메서드로 이동한다.
            } else if (key.isWritable()) {
              this.writeOP(key);
            }
          }
        }

      } else {
        System.out.println("서버 소켓을 생성하지 못했습니다.");
      }

    } catch (IOException e) {
      System.err.println(e);
    }
  }

  private void acceptOP(SelectionKey key, Selector selector) throws IOException {
    // 14. 연결 요청 이벤트가 발생한 채널은 항상 ServerSocketChannel이므로 이벤트가 발생한 채널을 ServerSocketChannel로 캐스팅한다.
    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
    // 15. ServerSocketChannel을 사용하여 클라이언트의 연결을 수락하고 연결된 소켓 채널을 가져온다.
    SocketChannel socketChannel = serverChannel.accept();
    // 16. 연결된 클라이언트 소켓 채널을 논블로킹 모드로 설정한다.
    socketChannel.configureBlocking(false);

    System.out.println("클라이언트 연결됨 : " + socketChannel.getRemoteAddress());

    keepDataTrack.put(socketChannel, new ArrayList<>());
    // 17. 클라이언트 소켓 채널을 Selector에 등록하여 I/O 이벤트를 감시한다.
    socketChannel.register(selector, SelectionKey.OP_READ);
  }

  private void readOP(SelectionKey key) {
    try {
      SocketChannel socketChannel = (SocketChannel) key.channel();
      buffer.clear();

      int numRead = -1;
      try {
        numRead = socketChannel.read(buffer);

      } catch (IOException e) {
        System.err.println("데이터 읽기 에러!");
      }

      if (numRead == -1) {
        this.keepDataTrack.remove(socketChannel);
        System.out.println("클라이언트 연결 종료 : " + socketChannel.getRemoteAddress());
        socketChannel.close();
        key.cancel();
        return;
      }

      byte[] data = new byte[numRead];
      System.arraycopy(buffer.array(), 0, data, 0, numRead);
      System.out.println(new String(data, StandardCharsets.UTF_8) + " from " + socketChannel.getRemoteAddress());

      doEchoJob(key, data);

    } catch (IOException e) {
      System.err.println(e);
    }
  }

  private void doEchoJob(SelectionKey key, byte[] data) {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    List<byte[]> channelData = this.keepDataTrack.get(socketChannel);
    channelData.add(data);

    key.interestOps(SelectionKey.OP_WRITE);
  }

  private void writeOP(SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    List<byte[]> channelData = this.keepDataTrack.get(socketChannel);
    Iterator<byte[]> it = channelData.iterator();

    while (it.hasNext()) {
      byte[] item = it.next();
      it.remove();
      socketChannel.write(ByteBuffer.wrap(item));
    }

    key.interestOps(SelectionKey.OP_READ);
  }

  public static void main(String[] args) {
    NonBlockingServer nonBlockingServer = new NonBlockingServer();
    nonBlockingServer.startEchoServer();
  }

}
