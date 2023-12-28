package reactor_single_threaded;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class EchoHandler implements ChannelHandler {

  static final String POISON_PILL = "BYE";

  final SocketChannel socketChannel;
  final Selector selector;
  final LinkedList<String> msgQ;
  final MsgCodec msgCodec;

  public EchoHandler(final SocketChannel socketChannel, final Selector selector) throws IOException {
    this.socketChannel = socketChannel;
    this.selector = selector;
    this.msgQ = new LinkedList<>();
    this.msgCodec = new MsgCodec();

    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ).attach(this);

    selector.wakeup();
  }

  @Override
  public void read() throws Exception {
    final var buffer = ByteBuffer.allocate(1024);
    socketChannel.read(buffer);
    final var msg = msgCodec.decode(buffer);

    System.out.println("<=== " + msg);
    msgQ.addLast(msg);

    socketChannel.register(selector, SelectionKey.OP_WRITE).attach(this);
    selector.wakeup();
  }

  @Override
  public void write() throws Exception {
    final var msg = msgQ.removeFirst();
    socketChannel.write(msgCodec.encode(msg));

    System.out.println("===> " + msg);

    if (POISON_PILL.equals(msg.trim())) {
      System.out.println("Closing " + socketChannel);
      socketChannel.close();
      return;
    }

    socketChannel.register(selector, SelectionKey.OP_READ).attach(this);
    selector.wakeup();
  }

  static class MsgCodec {
    ByteBuffer encode(final String msg) {
      return ByteBuffer.wrap(msg.getBytes());
    }

    String decode(final ByteBuffer buffer) {
      return new String(buffer.array(), buffer.arrayOffset(), buffer.remaining());
    }
  }
}
