package reactor_single_threaded;

public class EchoServer {

  public static void main(String[] args) throws Exception {
    new Reactor().bind(8080);
  }
}
