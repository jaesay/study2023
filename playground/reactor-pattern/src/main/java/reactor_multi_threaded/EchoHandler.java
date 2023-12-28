package reactor_multi_threaded;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class EchoHandler implements ChannelHandler {
  static final String POISON_PILL = "BYE";

  final Executor executor;
  final SocketChannel socketChannel;
  final MsgCodec msgCodec;
  final Selector selector;
  final LinkedBlockingQueue<String> msgQ;

  public EchoHandler(final SocketChannel socketChannel, final Selector selector) throws IOException {
    this.socketChannel = socketChannel;
    this.msgCodec = new MsgCodec();
    this.selector = selector;
    this.msgQ = new LinkedBlockingQueue<>();
    this.executor = Executors.newCachedThreadPool(r -> {
      final var thread = new Thread(r);
      thread.setName("Worker");
      return thread;
    });

    socketChannel.configureBlocking(false);
    socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE).attach(this);

    selector.wakeup();
  }

  @Override
  public void read() throws Exception {
    final var buffer = ByteBuffer.allocate(1024);
    socketChannel.read(buffer);
    final var msg = msgCodec.decode(buffer);
    System.out.println("[" + Thread.currentThread() + "] <=== " + msg);

    executor.execute(new Processor(msg)); // 블로킹 또는 오래걸리는 작업이기 때문에 별도의 쓰레드에서 동작하고 이벤트 루프는 계속 처리..
  }

  @Override
  public void write() throws Exception {
    if (msgQ.isEmpty()) {
      socketChannel.register(selector, SelectionKey.OP_READ).attach(this);
      return;
    }
    final var msg = msgQ.take();
    final var buffer = msgCodec.encode(msg);
    socketChannel.write(buffer);
    System.out.println("[" + Thread.currentThread() + "] ===> " + msg);

    if (POISON_PILL.equals(msg)) {
      System.out.println("Closing " + socketChannel);
      socketChannel.close();
    }
  }

  class Processor implements Runnable {
    private final String message;

    public Processor(String message) {
      this.message = message;
    }

    @Override
    public void run() {
      try {
        System.out.println("[" + Thread.currentThread() + "] is handling message [" + message + "]");
        Thread.sleep(3000L); // Do some time-consuming computations
        msgQ.put(message);
        socketChannel.register(selector, SelectionKey.OP_WRITE).attach(EchoHandler.this);
        selector.wakeup(); // Wake up the selector because it may be waiting for READ events, but we're not interested in READ now
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
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
