package reactor_multi_threaded;

public final class EchoServer {
  public static void main(String[] args) throws Exception {
    new Reactor().bind(8080);
  }
}
